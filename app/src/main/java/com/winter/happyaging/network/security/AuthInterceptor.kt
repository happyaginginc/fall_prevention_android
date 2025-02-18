package com.winter.happyaging.network.security

import android.content.Context
import com.winter.happyaging.network.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.coroutines.resume

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val tokenManager = TokenManager(context)
        var accessToken = tokenManager.getAccessToken()
        val requestWithToken = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(requestWithToken)
        if (response.code == 401) {
            synchronized(this) {
                if (tokenManager.getAccessToken() == accessToken) {
                    runBlocking {
                        if (refreshTokenBlocking(tokenManager)) {
                            accessToken = tokenManager.getAccessToken()
                            val newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer $accessToken")
                                .build()
                            response.close()
                            response = chain.proceed(newRequest)
                        }
                    }
                }
            }
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