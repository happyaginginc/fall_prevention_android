package com.winter.happyaging

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse
import com.winter.happyaging.ResDTO.RefreshTokenResponse
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.service.AuthService

// DataStore 선언
private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager(private val context: Context) {

    // 키 선언
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    // AccessToken 가져오기
    fun getAccessToken(): String? {
        return runBlocking {
            context.dataStore.data.map { it[ACCESS_TOKEN_KEY] ?: "" }.first()
        }
    }

    // RefreshToken 가져오기
    fun getRefreshToken(): String? {
        return runBlocking {
            context.dataStore.data.map { it[REFRESH_TOKEN_KEY] ?: "" }.first()
        }
    }

    // 토큰 저장 (영속성 보장)
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
        Log.d("TokenManager", "토큰 저장 완료: AccessToken: $accessToken, RefreshToken: $refreshToken")
    }

    // AccessToken 갱신
    fun refreshAccessToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.e("TokenManager", "RefreshToken 없음. 로그아웃 필요")
            onComplete(false)
            return
        }

        val authService = RetrofitClient.getInstance(context).create(AuthService::class.java)
        authService.refreshAccessToken("Bearer $refreshToken")
            .enqueue(object : Callback<RefreshTokenResponse> {
                override fun onResponse(
                    call: Call<RefreshTokenResponse>,
                    response: RetrofitResponse<RefreshTokenResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            runBlocking {
                                saveTokens(it.data.accessToken.value, it.data.refreshToken.value)
                            }
                            Log.d("TokenManager", "AccessToken 갱신 성공!")
                            onComplete(true)
                        }
                    } else {
                        Log.e("TokenManager", "AccessToken 갱신 실패")
                        onComplete(false)
                    }
                }

                override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
                    Log.e("TokenManager", "네트워크 오류: ${t.message}")
                    onComplete(false)
                }
            })
    }
}