package com.winter.happyaging.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R
import com.winter.happyaging.data.auth.model.request.LoginRequest
import com.winter.happyaging.data.auth.model.response.LoginResponse
import com.winter.happyaging.data.auth.service.AuthService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.TokenManager
import com.winter.happyaging.ui.auth.findAccount.FindAccountFragment
import com.winter.happyaging.ui.auth.register.RegisterFragment
import com.winter.happyaging.ui.home.HomeActivity
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

        // 뷰 초기화
        val scrollView: ScrollView = view.findViewById(R.id.scrollView)
        val tvFindEmailPassword: TextView = view.findViewById(R.id.tvFindEmailPassword)
        val tvSignUpLink: TextView = view.findViewById(R.id.signupLink)
        val btnLogin: Button = view.findViewById(R.id.loginButton)
        val etEmail: EditText = view.findViewById(R.id.emailEditText)
        val etPassword: EditText = view.findViewById(R.id.passwordEditText)
        val tvEmailError: TextView = view.findViewById(R.id.tvEmailError)
        val tvPasswordError: TextView = view.findViewById(R.id.tvPasswordError)

        // HTML 태그 적용 (필요시, 예를 들어 라벨에 별표가 포함되어 있다면)
        // 예: tvSignUpLink.text = Html.fromHtml(getString(R.string.signup_link), Html.FROM_HTML_MODE_LEGACY)

        // 실시간 유효성 검사: 이메일
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateEmail(etEmail, tvEmailError)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 실시간 유효성 검사: 비밀번호
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword(etPassword, tvPasswordError)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 로그인 버튼 클릭 시
        btnLogin.setOnClickListener {
            // 클릭 시 모든 에러 메시지 초기화
            tvEmailError.visibility = View.GONE
            tvPasswordError.visibility = View.GONE

            // 입력값 유효성 검사 재실행
            validateEmail(etEmail, tvEmailError)
            validatePassword(etPassword, tvPasswordError)

            // 에러가 있을 경우 API 호출 중단
            if (tvEmailError.visibility == View.VISIBLE || tvPasswordError.visibility == View.VISIBLE) {
                Toast.makeText(requireContext(), R.string.login_fill_fields, Toast.LENGTH_SHORT).show()
                // 에러가 있는 첫번째 필드로 스크롤 (선택사항)
                scrollView.post {
                    scrollView.smoothScrollTo(0, etEmail.top)
                }
                return@setOnClickListener
            }

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 로그인 API 호출
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

                                // TokenManager를 통해 토큰 저장
                                runBlocking {
                                    TokenManager(requireContext()).saveTokens(accessToken, refreshToken)
                                }

                                // 홈 화면으로 이동
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

        // 회원가입 화면 이동
        tvSignUpLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, RegisterFragment())
                addToBackStack(null)
            }
        }

        // 이메일/비밀번호 찾기 화면 이동
        tvFindEmailPassword.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, FindAccountFragment())
                addToBackStack(null)
            }
        }
    }

    // 이메일 유효성 검사 함수
    private fun validateEmail(etEmail: EditText, tvEmailError: TextView) {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            tvEmailError.text = "이메일은 필수값입니다."
            tvEmailError.visibility = View.VISIBLE
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.text = "올바른 이메일 형식이 아닙니다."
            tvEmailError.visibility = View.VISIBLE
        } else {
            tvEmailError.visibility = View.GONE
        }
    }

    // 비밀번호 유효성 검사 함수
    private fun validatePassword(etPassword: EditText, tvPasswordError: TextView) {
        val password = etPassword.text.toString().trim()
        if (password.isEmpty()) {
            tvPasswordError.text = "비밀번호는 필수값입니다."
            tvPasswordError.visibility = View.VISIBLE
        } else {
            tvPasswordError.visibility = View.GONE
        }
    }
}
