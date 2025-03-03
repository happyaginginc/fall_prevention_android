package com.winter.happyaging.network.security

import android.content.Context
import android.util.Log
import com.winter.happyaging.network.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.resume

class AuthInterceptor(private val context: Context) : Interceptor {

    @Volatile
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // [중요] 직접 붙인 Authorization 헤더를 발견해도 그냥 넘어가지 않게 처리
        // (아래 조건문 삭제/주석)
        // if (originalRequest.header("Authorization") != null) {
        //     return chain.proceed(originalRequest)
        // }

        val tokenManager = TokenManager(context)
        val oldAccessToken = tokenManager.getAccessToken() // 만료될 수도 있는 값

        // 1) 무조건 "Bearer <토큰>" 붙여서 진행
        val requestWithToken = originalRequest.newBuilder()
            .removeHeader("Authorization") // 혹시 기존 헤더가 있어도 제거해버림
            .addHeader("Authorization", "Bearer $oldAccessToken")
            .build()

        var response = chain.proceed(requestWithToken)

        // 2) 만약 401이 뜨면 => Refresh 시도
        if (response.code == 401) {
            synchronized(this) {
                if (!isRefreshing) {
                    isRefreshing = true
                    val stillSameToken = (tokenManager.getAccessToken() == oldAccessToken)
                    if (stillSameToken) {
                        // 만료된 액세스 토큰을 아직도 쓰고 있으면 refresh 로직 실행
                        val refreshSuccess = runBlocking {
                            refreshTokenBlocking(tokenManager)
                        }
                        if (!refreshSuccess) {
                            isRefreshing = false
                            // refresh 실패 시에는 그냥 이전 response 그대로 리턴
                            // (로그아웃 처리)
                            return response
                        }
                    }
                    isRefreshing = false
                }
            }
            // refresh 성공했다면, 새 토큰 다시 가져와 재요청
            val newAccessToken = tokenManager.getAccessToken()
            response.close() // 기존 응답은 닫기

            val newRequest = originalRequest.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $newAccessToken")
                .build()
            response = chain.proceed(newRequest)
        }

        return response
    }

    private suspend fun refreshTokenBlocking(tokenManager: TokenManager): Boolean =
        suspendCancellableCoroutine { cont ->
            tokenManager.refreshAccessToken { success ->
                if (!success) {
                    // refresh 실패
                    runBlocking { tokenManager.clearTokens() }
                }
                cont.resume(success)
            }
        }
}