package com.winter.happyaging.ui.splash

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
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

class SplashActivity : ComponentActivity() {

    private val tokenManager by lazy { TokenManager(this) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (!isNetworkConnected()) {
            showNetworkFailDialog {
                if (isNetworkConnected()) {
                    checkAutoLogin()
                } else {
                    showNetworkFailDialog { checkAutoLogin() }
                }
            }
            return
        }

        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()

        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            Log.d("SplashActivity", "토큰이 존재하지 않음. 로그인 화면으로 이동.")
            navigateToIntro()
            return
        }

        Log.d("SplashActivity", "토큰 확인 완료. 유저 정보 요청 시작.")

        val authService = RetrofitClient.getInstance(this).create(AuthService::class.java)
        authService.getUserInfo().enqueue(object : Callback<ApiResponse<UserInfoResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<UserInfoResponse>>,
                response: Response<ApiResponse<UserInfoResponse>>
            ) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    val userInfo = response.body()?.data
                    if (userInfo != null) {
                        Log.d("SplashActivity", "유저 정보 로드 성공: ${userInfo.email}")
                        UserProfileManager.saveUserInfo(
                            this@SplashActivity,
                            userInfo.id,
                            userInfo.email,
                            userInfo.name,
                            userInfo.phoneNumber
                        )
                        navigateToHome()
                    } else {
                        Log.d("SplashActivity", "유저 정보가 없음. 로그인 화면으로 이동.")
                        navigateToIntro()
                    }
                } else {
                    Log.d("SplashActivity", "응답 실패 또는 상태 코드 오류: ${response.code()}")
                    // 재시도 다이얼로그를 통해 다시 시도
                    showNetworkFailDialog { checkAutoLogin() }
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserInfoResponse>>, t: Throwable) {
                Log.e("SplashActivity", "네트워크 요청 실패: ${t.localizedMessage}")
                // 재시도 다이얼로그를 통해 다시 시도
                showNetworkFailDialog { checkAutoLogin() }
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

    private fun showNetworkFailDialog(retryAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("네트워크 오류")
            .setMessage("인터넷 연결 상태가 좋지 않거나 서버에 접속할 수 없습니다. 네트워크 상태를 확인 후 다시 시도해 주세요.")
            .setPositiveButton("재시도") { dialog, _ ->
                dialog.dismiss()
                retryAction()
            }
            .setNegativeButton("종료") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
