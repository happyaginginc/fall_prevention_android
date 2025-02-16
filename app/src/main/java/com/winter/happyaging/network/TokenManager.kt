package com.winter.happyaging.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.winter.happyaging.ResDTO.RefreshTokenResponse
import com.winter.happyaging.auth.service.AuthService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore by preferencesDataStore("auth_prefs")

/**
 * 앱 내 토큰 관리: DataStore 사용
 */
class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private const val TAG = "TokenManager"
    }

    // AccessToken 가져오기
    fun getAccessToken(): String? {
        return runBlocking {
            context.dataStore.data
                .map { it[ACCESS_TOKEN_KEY] }
                .first()
        }
    }

    // RefreshToken 가져오기
    fun getRefreshToken(): String? {
        return runBlocking {
            context.dataStore.data
                .map { it[REFRESH_TOKEN_KEY] }
                .first()
        }
    }

    // 토큰 저장
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
        // 필요 시, Log.d(TAG, "토큰 저장 완료") 정도만 남길 수 있음.
    }

    // AccessToken 갱신
    fun refreshAccessToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            // 로그아웃 등 추가 처리 가능
            onComplete(false)
            return
        }

        val authService = RetrofitClient.getInstance(context).create(AuthService::class.java)
        authService.refreshAccessToken("Bearer $refreshToken").enqueue(object : Callback<RefreshTokenResponse> {
            override fun onResponse(call: Call<RefreshTokenResponse>, response: Response<RefreshTokenResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        runBlocking {
                            saveTokens(it.data.accessToken.value, it.data.refreshToken.value)
                        }
                        onComplete(true)
                    } ?: onComplete(false)
                } else {
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
                onComplete(false)
            }
        })
    }
}