package com.winter.happyaging.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.winter.happyaging.data.auth.model.response.UserInfoResponse
import com.winter.happyaging.data.auth.service.AuthService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.TokenManager
import com.winter.happyaging.network.UserProfileManager
import com.winter.happyaging.network.model.ApiResponse
import com.winter.happyaging.ui.home.HomeActivity
import com.winter.happyaging.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()

        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            navigateToIntro()
            return
        }

        val authService = RetrofitClient.getInstance(this).create(AuthService::class.java)
        authService.getUserInfo("Bearer $accessToken").enqueue(object : Callback<ApiResponse<UserInfoResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<UserInfoResponse>>,
                response: Response<ApiResponse<UserInfoResponse>>
            ) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    response.body()?.data?.let { userInfo ->
                        UserProfileManager.saveUserInfo(
                            this@SplashActivity,
                            userInfo.id,
                            userInfo.email,
                            userInfo.name,
                            userInfo.phoneNumber
                        )
                        navigateToHome()
                    } ?: navigateToIntro()
                } else {
                    navigateToIntro()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserInfoResponse>>, t: Throwable) {
                navigateToIntro()
            }
        })
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun navigateToIntro() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}