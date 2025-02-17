package com.winter.happyaging.ui.auth.register

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
import com.winter.happyaging.data.auth.model.request.RegisterRequest
import com.winter.happyaging.data.auth.model.response.RegisterResponse
import com.winter.happyaging.data.auth.service.AuthService
import com.winter.happyaging.ui.auth.login.LoginFragment
import com.winter.happyaging.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 회원가입 화면
 */
class RegisterFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvTermsDetail: TextView
    private lateinit var tvAlreadyHaveAccount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        tvTermsDetail = view.findViewById(R.id.tvTermsDetail)
        tvAlreadyHaveAccount = view.findViewById(R.id.loginLink)

        // [1] "내용 보기" 클릭 시 약관 화면 이동
        tvTermsDetail.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, TermsAgreementFragment())
                addToBackStack(null)
            }
        }

        // [2] 회원가입 버튼 클릭
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(
                email = email,
                password = password,
                name = name,
                phoneNumber = phoneNumber
            )

            val authService = RetrofitClient.getInstance(requireContext())
                .create(AuthService::class.java)

            authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    // 성공 처리
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            if (authResponse.status == 201) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.register_success, // 회원가입 성공
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 가입 완료 화면으로 이동
                                parentFragmentManager.commit {
                                    replace(
                                        R.id.fragmentContainer,
                                        RegisterCompleteFragment.newInstance(name)
                                    )
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
                    Toast.makeText(
                        requireContext(),
                        R.string.network_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        // [3] 이미 계정이 있는 경우 → 로그인 화면
        tvAlreadyHaveAccount.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, LoginFragment())
                addToBackStack(null)
            }
        }
    }
}