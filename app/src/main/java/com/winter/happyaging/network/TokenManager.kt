package com.winter.happyaging.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.winter.happyaging.data.auth.service.AuthService
import com.winter.happyaging.network.security.model.RefreshTokenResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    fun getAccessToken(): String? = runBlocking {
        context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }.first()
    }

    fun getRefreshToken(): String? = runBlocking {
        context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    fun refreshAccessToken(onComplete: (Boolean) -> Unit) {
        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            onComplete(false)
            return
        }

        val authService = RetrofitClient.getInstance(context).create(AuthService::class.java)
        authService.refreshAccessToken("Bearer $refreshToken").enqueue(object : Callback<RefreshTokenResponse> {
            override fun onResponse(call: Call<RefreshTokenResponse>, response: Response<RefreshTokenResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { refreshResponse ->
                        runBlocking {
                            saveTokens(
                                refreshResponse.data.accessToken.value,
                                refreshResponse.data.refreshToken.value
                            )
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

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }
}
