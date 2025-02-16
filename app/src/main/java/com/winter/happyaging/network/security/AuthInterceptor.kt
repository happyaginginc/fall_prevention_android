package com.winter.happyaging.network.security

import android.content.Context
import com.winter.happyaging.network.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.resume

/**
 * OkHttp Interceptor:
 * 401 응답 시 리프레시 토큰 갱신 후 재요청
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenManager = TokenManager(context)
        var accessToken = tokenManager.getAccessToken()

        val originalRequest = chain.request()
        val requestWithToken = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(requestWithToken)

        // 401 에러 발생 시 -> 토큰 재발급 후 재요청
        if (response.code == 401) {
            synchronized(this) {
                val latestToken = tokenManager.getAccessToken()
                if (latestToken == accessToken) {
                    // 최신 토큰이 동일하면 -> 새 토큰 요청
                    runBlocking {
                        val refreshSuccess = refreshTokenBlocking(tokenManager)
                        if (refreshSuccess) {
                            // 갱신된 토큰으로 재요청
                            accessToken = tokenManager.getAccessToken()
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

        return response
    }

    // 비동기 refreshAccessToken을 동기적으로 호출
    private suspend fun refreshTokenBlocking(tokenManager: TokenManager): Boolean {
        return suspendCancellableCoroutine { continuation ->
            tokenManager.refreshAccessToken { success ->
                continuation.resume(success)
            }
        }
    }
}