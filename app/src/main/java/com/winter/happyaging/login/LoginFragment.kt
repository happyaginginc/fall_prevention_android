package com.winter.happyaging.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R
import com.winter.happyaging.ReqDTO.LoginRequest
import com.winter.happyaging.ResDTO.LoginResponse
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.TokenManager
import com.winter.happyaging.home.HomeActivity
import com.winter.happyaging.service.AuthService
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFindEmailPassword = view.findViewById<TextView>(R.id.tvFindEmailPassword)
        val tvSignUpLink = view.findViewById<TextView>(R.id.signupLink)
        val btnLogin = view.findViewById<Button>(R.id.loginButton)
        val etEmail = view.findViewById<EditText>(R.id.emailEditText)
        val etPassword = view.findViewById<EditText>(R.id.passwordEditText)

        tvFindEmailPassword.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, FindAccountFragment())
                addToBackStack(null)
            }
        }

        tvSignUpLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, SignUpFragment())
                addToBackStack(null)
            }
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val authService = RetrofitClient.getInstance(requireContext()).create(AuthService::class.java)
            val loginRequest = LoginRequest(email, password)

            authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { loginResponse ->
                            Log.d("LoginFragment", "로그인 응답 데이터: $loginResponse")

                            if (loginResponse.status == 200) { // 백엔드에서 성공 코드 확인
                                Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()

                                val accessToken = loginResponse.data.accessToken.value
                                val refreshToken = loginResponse.data.refreshToken.value

                                Log.d("LoginFragment", "🚀 AccessToken: $accessToken, RefreshToken: $refreshToken")

                                runBlocking {
                                    val tokenManager = TokenManager(requireContext())
                                    tokenManager.saveTokens(accessToken, refreshToken) // 토큰 저장
                                }

                                Thread.sleep(1000) // DataStore 저장이 완료될 시간을 주기 위해 잠시 대기
                                val savedToken = TokenManager(requireContext()).getAccessToken()
                                Log.d("LoginFragment", "저장된 토큰 확인: $savedToken")

                                val intent = Intent(requireContext(), HomeActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "로그인 실패: ${loginResponse.status}, 메시지: ${loginResponse.data}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(
                                    "LoginFragment",
                                    "로그인 실패: ${loginResponse.status}, 메시지: ${loginResponse.data}"
                                )
                            }
                        } ?: run {
                            Log.e("LoginFragment", "로그인 실패: 응답 본문이 null")
                            Toast.makeText(requireContext(), "로그인 실패: 응답 본문이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("LoginFragment", "로그인 실패: ${response.code()}, 에러 내용: $errorBody")
                        Toast.makeText(requireContext(), "로그인 실패: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginFragment", "네트워크 오류 발생: ${t.message}")
                    Toast.makeText(requireContext(), "네트워크 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // 토큰 저장 메서드 수정 (SharedPreferences 사용)
    private fun saveToken(accessToken: String, refreshToken: String) {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply() // commit()
        }
        Log.d("LoginFragment", "토큰이 저장되었습니다: accessToken=$accessToken, refreshToken=$refreshToken")
    }

    // 저장된 토큰 가져오기
    private fun getAuthToken(): String {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("access_token", "") ?: ""
        if (token.isEmpty()) {
            Log.e("LoginFragment", "저장된 토큰이 없습니다!")
        } else {
            Log.d("LoginFragment", "불러온 토큰: $token")
        }
        return token
    }
}
