package com.winter.happyaging

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Request
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenManager = TokenManager(context)
        var accessToken = tokenManager.getAccessToken()

        val originalRequest = chain.request()

        val requestWithToken = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(requestWithToken)

        // 401 에러 발생 시 -> 토큰 재발급 후 다시 요청
        if (response.code == 401) {
            synchronized(this) {
                val updatedToken = tokenManager.getAccessToken() // 최신 토큰 가져오기

                // 최신 토큰이 기존 토큰과 동일하면 -> 새 토큰 요청
                if (updatedToken == accessToken) {
                    runBlocking {
                        val refreshSuccess = refreshTokenBlocking(tokenManager)
                        if (refreshSuccess) {
                            accessToken = tokenManager.getAccessToken() // 갱신된 토큰 가져오기

                            // 새 토큰으로 기존 요청 다시 실행
                            val newRequest = originalRequest.newBuilder()
                                .addHeader("Authorization", "Bearer $accessToken")
                                .build()

                            response.close() // 기존 응답 닫기
                            response = chain.proceed(newRequest)
                        }
                    }
                }
            }
        }

        return response // 최종 응답 반환
    }

    // 비동기 refreshAccessToken을 동기적으로 실행하는 함수
    private suspend fun refreshTokenBlocking(tokenManager: TokenManager): Boolean {
        return suspendCancellableCoroutine { continuation ->
            tokenManager.refreshAccessToken { success ->
                continuation.resume(success)
            }
        }
    }
}
