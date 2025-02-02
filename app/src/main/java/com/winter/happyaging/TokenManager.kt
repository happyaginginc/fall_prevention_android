package com.winter.happyaging

import android.content.Context
import android.util.Log
import com.winter.happyaging.ReqDTO.RefreshTokenRequest
import com.winter.happyaging.ResDTO.RefreshTokenResponse
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.service.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenManager(private val context: Context) {

    private val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun getAccessToken(): String? {
        return sharedPref.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPref.getString("refresh_token", null)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        with(sharedPref.edit()) {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun refreshAccessToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.e("TokenManager", "refreshToken이 없습니다.")
            onComplete(false)
            return
        }

        val authService = RetrofitClient.getInstance(context).create(AuthService::class.java)
        val request = RefreshTokenRequest(refreshToken)

        authService.refreshAccessToken(request).enqueue(object : Callback<RefreshTokenResponse> {
            override fun onResponse(call: Call<RefreshTokenResponse>, response: Response<RefreshTokenResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { refreshResponse ->
                        if (refreshResponse.status == 200) {
                            val newAccessToken = refreshResponse.data.accessToken.value
                            val newRefreshToken = refreshResponse.data.refreshToken.value

                            saveTokens(newAccessToken, newRefreshToken)
                            Log.d("TokenManager", "새로운 토큰 저장 완료")
                            onComplete(true)
                        } else {
                            Log.e("TokenManager", "토큰 갱신 실패: 상태 코드 ${refreshResponse.status}")
                            onComplete(false)
                        }
                    }
                } else {
                    Log.e("TokenManager", "토큰 갱신 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
                Log.e("TokenManager", "토큰 갱신 요청 실패: ${t.message}")
                onComplete(false)
            }
        })
    }

    fun clearTokens() {
        with(sharedPref.edit()) {
            remove("access_token")
            remove("refresh_token")
            apply()
        }
    }
}
