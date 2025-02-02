package com.winter.happyaging

import android.content.Context
import com.winter.happyaging.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenManager = TokenManager(context)
        val accessToken = tokenManager.getAccessToken()

        // 기존 요청에 AccessToken 추가
        val originalRequest = chain.request()
        val requestWithToken = originalRequest.newBuilder()
            .apply {
                if (!accessToken.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $accessToken")
                }
            }
            .build()

        // 기존 요청을 실행
        var response = chain.proceed(requestWithToken)

        // 401 Unauthorized 발생 시 -> 토큰 재발급 시도
        if (response.code == 401) {
            synchronized(this) { // 여러 요청이 동시에 실행될 경우 동기화
                val newAccessToken = tokenManager.getAccessToken() // 최신 토큰 가져오기
                if (newAccessToken == accessToken) { // 기존 토큰과 동일하면 갱신 필요
                    tokenManager.refreshAccessToken { success ->
                        if (success) {
                            val updatedAccessToken = tokenManager.getAccessToken()
                            val newRequest = originalRequest.newBuilder()
                                .addHeader("Authorization", "Bearer $updatedAccessToken")
                                .build()
                            response = chain.proceed(newRequest) // ✅ 새로운 요청 실행
                        }
                    }
                }
            }
        }

        return response // ✅ 최종 응답 반환
    }
}
