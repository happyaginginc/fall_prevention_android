package com.winter.happyaging.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.winter.happyaging.R
import com.winter.happyaging.data.auth.model.response.UserInfoResponse
import com.winter.happyaging.data.auth.service.AuthService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.TokenManager
import com.winter.happyaging.network.UserProfileManager
import com.winter.happyaging.network.model.ApiResponse
import com.winter.happyaging.ui.auth.IntroFragment
import com.winter.happyaging.ui.home.HomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBarMain)

        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()

        // 1) 토큰이 없으면 -> IntroFragment
        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            showIntroFragment()
            return
        }

        // 2) 토큰이 있으면 -> 서버에 사용자 정보 요청
        progressBar.visible()
        val authService = RetrofitClient.getInstance(this).create(AuthService::class.java)
        authService.getUserInfo("Bearer $accessToken").enqueue(object : Callback<ApiResponse<UserInfoResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<UserInfoResponse>>,
                response: Response<ApiResponse<UserInfoResponse>>
            ) {
                progressBar.gone()
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.status == 200) {
                            // 사용자 정보가 정상적으로 오면 바로 HomeActivity
                            val userInfo = body.data
                            UserProfileManager.saveUserInfo(
                                this@MainActivity,
                                userInfo.id,
                                userInfo.email,
                                userInfo.name,
                                userInfo.phoneNumber
                            )
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            showIntroFragment()
                        }
                    } ?: run {
                        showIntroFragment()
                    }
                } else {
                    showIntroFragment()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserInfoResponse>>, t: Throwable) {
                progressBar.gone()
                showIntroFragment()
            }
        })
    }

    /**
     * IntroFragment 표시 함수
     */
    private fun showIntroFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, IntroFragment())
            .commit()
    }

    // 편의용 확장 함수
    private fun ProgressBar.visible() { this.visibility = View.VISIBLE }
    private fun ProgressBar.gone() { this.visibility = View.GONE }

    /**
     * 외부에서 특정 Fragment 로 전환이 필요할 때 사용할 수 있는 함수
     */
    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}