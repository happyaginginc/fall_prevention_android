package com.winter.happyaging.network.security

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.winter.happyaging.network.TokenManager
import com.winter.happyaging.ui.main.MainActivity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.resume

class AuthInterceptor(private val context: Context) : Interceptor {

    @Volatile
    private var isRefreshing = false
    private val TAG = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.d(TAG, "Intercepting request: ${originalRequest.url}")

        // /auth/refresh 요청 자체는 토큰 재발급 전용이므로 헤더 추가/재시도 로직 제외
        val originalUrl = originalRequest.url.toString()
        if (originalUrl.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val tokenManager = TokenManager(context)
        val oldAccessToken = tokenManager.getAccessToken()
        Log.d(TAG, "Current access token: $oldAccessToken")

        // 기존 AccessToken 부착
        val requestWithToken = originalRequest.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $oldAccessToken")
            .build()

        var response = chain.proceed(requestWithToken)
        Log.d(TAG, "Response received with status code: ${response.code}")

        // AccessToken 만료로 인한 401 => Refresh 시도
        if (response.code == 401) {
            Log.d(TAG, "Unauthorized response detected, attempting to refresh token")
            synchronized(this) {
                if (!isRefreshing) {
                    isRefreshing = true
                    val stillSameToken = (tokenManager.getAccessToken() == oldAccessToken)
                    if (stillSameToken) {
                        Log.d(TAG, "Access token is still the same, refreshing token")
                        val refreshSuccess = runBlocking {
                            refreshTokenBlocking(tokenManager)
                        }
                        if (!refreshSuccess) {
                            isRefreshing = false
                            Log.d(TAG, "Token refresh failed, clearing tokens and moving to Login")

                            runBlocking { tokenManager.clearTokens() }

                            Handler(Looper.getMainLooper()).post {
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                                context.startActivity(intent)
                            }

                            return response
                        }
                    }
                    isRefreshing = false
                }
            }

            Log.d(TAG, "Token refresh successful, retrying request with new token")
            val newAccessToken = tokenManager.getAccessToken()
            response.close()

            val newRequest = originalRequest.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $newAccessToken")
                .build()
            response = chain.proceed(newRequest)
            Log.d(TAG, "Retried request with new token, received status code: ${response.code}")
        }
        return response
    }

    private suspend fun refreshTokenBlocking(tokenManager: TokenManager): Boolean =
        suspendCancellableCoroutine { cont ->
            tokenManager.refreshAccessToken { success ->
                Log.d(TAG, "Refresh token callback: success=$success")
                cont.resume(success)
            }
        }
}