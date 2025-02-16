package com.winter.happyaging.auth.ui.login

import android.content.Intent
import android.os.Bundle
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
import com.winter.happyaging.auth.model.request.LoginRequest
import com.winter.happyaging.auth.model.response.LoginResponse
import com.winter.happyaging.auth.service.AuthService
import com.winter.happyaging.auth.ui.findAccount.FindAccountFragment
import com.winter.happyaging.auth.ui.register.RegisterFragment
import com.winter.happyaging.home.HomeActivity
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.TokenManager
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 로그인 화면
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFindEmailPassword: TextView = view.findViewById(R.id.tvFindEmailPassword)
        val tvSignUpLink: TextView = view.findViewById(R.id.signupLink)
        val btnLogin: Button = view.findViewById(R.id.loginButton)
        val etEmail: EditText = view.findViewById(R.id.emailEditText)
        val etPassword: EditText = view.findViewById(R.id.passwordEditText)

        // 이메일/비밀번호 찾기
        tvFindEmailPassword.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, FindAccountFragment())
                addToBackStack(null)
            }
        }

        // 회원가입 화면 이동
        tvSignUpLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, RegisterFragment())
                addToBackStack(null)
            }
        }

        // 로그인 요청
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), R.string.login_fill_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val authService = RetrofitClient.getInstance(requireContext()).create(AuthService::class.java)
            val loginRequest = LoginRequest(email, password)

            authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { loginResponse ->
                            if (loginResponse.status == 200) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.login_success,
                                    Toast.LENGTH_SHORT
                                ).show()

                                val accessToken = loginResponse.data.accessToken.value
                                val refreshToken = loginResponse.data.refreshToken.value

                                // TokenManager 통해 토큰 저장
                                runBlocking {
                                    TokenManager(requireContext()).saveTokens(accessToken, refreshToken)
                                }

                                // 홈 화면 진입
                                startActivity(Intent(requireContext(), HomeActivity::class.java))
                                requireActivity().finish()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.login_fail_status, loginResponse.status),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } ?: run {
                            Toast.makeText(
                                requireContext(),
                                R.string.login_fail_empty_body,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: getString(R.string.unknown_error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_fail_server, errorBody),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        R.string.network_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}