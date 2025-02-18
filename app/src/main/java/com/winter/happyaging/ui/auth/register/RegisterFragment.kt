package com.winter.happyaging.ui.auth.register

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R
import com.winter.happyaging.data.auth.model.request.RegisterRequest
import com.winter.happyaging.data.auth.model.response.RegisterResponse
import com.winter.happyaging.data.auth.service.AuthService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.ui.auth.login.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 회원가입 화면
 */
class RegisterFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var tvNameError: TextView
    private lateinit var etPhoneNumber: EditText
    private lateinit var tvPhoneError: TextView
    private lateinit var etEmail: EditText
    private lateinit var tvEmailError: TextView
    private lateinit var etPassword: EditText
    private lateinit var tvPasswordError: TextView
    private lateinit var etPasswordConfirm: EditText
    private lateinit var tvPasswordConfirmError: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_signup.xml 레이아웃 사용
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 클래스 프로퍼티에 뷰 할당
        etName = view.findViewById(R.id.etName)
        tvNameError = view.findViewById(R.id.tvNameError)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        tvPhoneError = view.findViewById(R.id.tvPhoneError)
        etEmail = view.findViewById(R.id.etEmail)
        tvEmailError = view.findViewById(R.id.tvEmailError)
        etPassword = view.findViewById(R.id.etPassword)
        tvPasswordError = view.findViewById(R.id.tvPasswordError)
        etPasswordConfirm = view.findViewById(R.id.etPasswordConfirm)
        tvPasswordConfirmError = view.findViewById(R.id.tvPasswordConfirmError)

        val scrollView: ScrollView = view.findViewById(R.id.scrollView)
        val privacyCheckBox: CheckBox = view.findViewById(R.id.privacyCheckBox)
        val tvPrivacyError: TextView = view.findViewById(R.id.tvPrivacyError)
        val btnSignUp: Button = view.findViewById(R.id.btnSignUp)
        val tvTermsDetail: TextView = view.findViewById(R.id.tvTermsDetail)
        val loginLink: TextView = view.findViewById(R.id.loginLink)

        // HTML 텍스트가 포함된 문자열을 Html.fromHtml()로 적용
        val tvRequiredNote: TextView = view.findViewById(R.id.tvRequiredNote)
        tvRequiredNote.text = Html.fromHtml(getString(R.string.required_note), Html.FROM_HTML_MODE_LEGACY)

        val tvNameLabel: TextView = view.findViewById(R.id.tvNameLabel)
        tvNameLabel.text = Html.fromHtml(getString(R.string.name_label), Html.FROM_HTML_MODE_LEGACY)

        val tvPhoneLabel: TextView = view.findViewById(R.id.tvPhoneLabel)
        tvPhoneLabel.text = Html.fromHtml(getString(R.string.phone_label), Html.FROM_HTML_MODE_LEGACY)

        val tvEmailLabel: TextView = view.findViewById(R.id.tvEmailLabel)
        tvEmailLabel.text = Html.fromHtml(getString(R.string.email_label), Html.FROM_HTML_MODE_LEGACY)

        val tvPasswordLabel: TextView = view.findViewById(R.id.tvPasswordLabel)
        tvPasswordLabel.text = Html.fromHtml(getString(R.string.password_label), Html.FROM_HTML_MODE_LEGACY)

        val tvPasswordConfirmLabel: TextView = view.findViewById(R.id.tvPasswordConfirmLabel)
        tvPasswordConfirmLabel.text = Html.fromHtml(getString(R.string.password_confirm_label), Html.FROM_HTML_MODE_LEGACY)

        // TextWatcher를 통해 실시간 유효성 검사 적용
        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateName()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePhone()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateEmail()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
                // 비밀번호가 변경되면 비밀번호 확인도 함께 검증
                validatePasswordConfirm()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordConfirm()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        // [1] "내용 보기" 클릭 시 약관 화면으로 이동
        tvTermsDetail.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, TermsAgreementFragment())
                addToBackStack(null)
            }
        }

        // [2] 회원가입 버튼 클릭 시 입력 검증 후 API 호출
        btnSignUp.setOnClickListener {
            // 클릭 시 모든 에러 메시지를 초기화
            tvNameError.visibility = View.GONE
            tvPhoneError.visibility = View.GONE
            tvEmailError.visibility = View.GONE
            tvPasswordError.visibility = View.GONE
            tvPasswordConfirmError.visibility = View.GONE
            tvPrivacyError.visibility = View.GONE

            // 각 필드의 유효성 검사 재실행
            validateName()
            validatePhone()
            validateEmail()
            validatePassword()
            validatePasswordConfirm()

            // 개인정보 수집 동의 검증
            if (!privacyCheckBox.isChecked) {
                tvPrivacyError.text = "개인정보 수집에 동의해 주세요."
                tvPrivacyError.visibility = View.VISIBLE
            }

            // 모든 에러가 보이지 않으면(=모두 통과) 서버 요청 진행
            if (tvNameError.visibility == View.GONE &&
                tvPhoneError.visibility == View.GONE &&
                tvEmailError.visibility == View.GONE &&
                tvPasswordError.visibility == View.GONE &&
                tvPasswordConfirmError.visibility == View.GONE &&
                tvPrivacyError.visibility == View.GONE) {

                val name = etName.text.toString().trim()
                val phoneNumber = etPhoneNumber.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                val registerRequest = RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    phoneNumber = phoneNumber
                )

                val authService = RetrofitClient.getInstance(requireContext())
                    .create(AuthService::class.java)

                authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { authResponse ->
                                if (authResponse.status == 201) {
                                    Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.commit {
                                        replace(R.id.fragmentContainer, RegisterCompleteFragment.newInstance(name))
                                        addToBackStack(null)
                                    }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.register_fail_status, authResponse.status),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: getString(R.string.unknown_error)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.register_fail, errorBody),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // 유효성 미통과 시 해당 필드로 스크롤
                scrollView.post {
                    // 예시: 이름 입력 필드로 스크롤 (필요시 focusView 로직 추가)
                    scrollView.smoothScrollTo(0, etName.top)
                }
                Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
            }
        }

        // [3] 로그인 링크 클릭 시 로그인 화면으로 이동
        loginLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, LoginFragment())
                addToBackStack(null)
            }
        }
    }

    // --- 실시간 유효성 검사 함수들 ---

    private fun validateName() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            tvNameError.text = "이름은 필수값입니다."
            tvNameError.visibility = View.VISIBLE
        } else {
            tvNameError.visibility = View.GONE
        }
    }

    private fun validatePhone() {
        val phone = etPhoneNumber.text.toString().trim()
        val phonePattern = Regex("^010-\\d{4}-\\d{4}\$")
        if (phone.isEmpty()) {
            tvPhoneError.text = "휴대폰 번호는 필수값입니다."
            tvPhoneError.visibility = View.VISIBLE
        } else if (!phonePattern.matches(phone)) {
            tvPhoneError.text = "휴대폰 번호 양식이 올바르지 않습니다. 예) 010-1234-5678"
            tvPhoneError.visibility = View.VISIBLE
        } else {
            tvPhoneError.visibility = View.GONE
        }
    }

    private fun validateEmail() {
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

    private fun validatePassword() {
        val password = etPassword.text.toString().trim()
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
        if (password.isEmpty()) {
            tvPasswordError.text = "비밀번호는 필수값입니다."
            tvPasswordError.visibility = View.VISIBLE
        } else if (!passwordPattern.matches(password)) {
            tvPasswordError.text = "비밀번호는 최소 8자리이며, 대소문자, 숫자, 특수문자를 포함해야 합니다."
            tvPasswordError.visibility = View.VISIBLE
        } else {
            tvPasswordError.visibility = View.GONE
        }
    }

    private fun validatePasswordConfirm() {
        val password = etPassword.text.toString().trim()
        val passwordConfirm = etPasswordConfirm.text.toString().trim()
        if (passwordConfirm.isEmpty()) {
            tvPasswordConfirmError.text = "비밀번호 확인은 필수값입니다."
            tvPasswordConfirmError.visibility = View.VISIBLE
        } else if (password != passwordConfirm) {
            tvPasswordConfirmError.text = "비밀번호가 일치하지 않습니다."
            tvPasswordConfirmError.visibility = View.VISIBLE
        } else {
            tvPasswordConfirmError.visibility = View.GONE
        }
    }
}