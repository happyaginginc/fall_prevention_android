package com.winter.happyaging.network.security

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
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

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.url.toString().contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val tokenManager = TokenManager(context)
        val oldAccessToken = tokenManager.getAccessToken()
        val requestWithToken = originalRequest.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $oldAccessToken")
            .build()

        var response = chain.proceed(requestWithToken)

        if (response.code == 401) {
            synchronized(this) {
                if (!isRefreshing) {
                    isRefreshing = true
                    if (tokenManager.getAccessToken() == oldAccessToken) {
                        val refreshSuccess = runBlocking { refreshTokenBlocking(tokenManager) }
                        if (!refreshSuccess) {
                            isRefreshing = false
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
            val newAccessToken = tokenManager.getAccessToken()
            response.close()
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
                cont.resume(success)
            }
        }
}
