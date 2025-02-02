package com.winter.happyaging.login

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
import com.winter.happyaging.ReqDTO.RegisterRequest
import com.winter.happyaging.ResDTO.RegisterResponse
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.service.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTermsDetail = view.findViewById<TextView>(R.id.tvTermsDetail)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etPhoneNumber = view.findViewById<EditText>(R.id.etPhoneNumber)
        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)

        // "내용 보기" 클릭 시 TermsFragment로 이동
        tvTermsDetail.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, TermsFragment())
                addToBackStack(null)
            }
        }

        // 회원가입 버튼 클릭 이벤트 처리
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 회원가입 요청 데이터 생성
            val registerRequest = RegisterRequest(
                email = email,
                password = password,
                name = name,
                phoneNumber = phoneNumber
            )

            // Retrofit 인스턴스 생성
            val authService = RetrofitClient.getInstance(requireContext()).create(AuthService::class.java)

            // 서버 요청
            authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    Log.d("RegisterRequest", "Request Data: $registerRequest")

                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            if (authResponse.status == 201) {
                                Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()

                                // 완료 화면으로 이동
                                val completeFragment = SignUpCompleteFragment.newInstance(name)
                                parentFragmentManager.commit {
                                    replace(R.id.fragmentContainer, completeFragment)
                                    addToBackStack(null)
                                }
                            } else {
                                Toast.makeText(requireContext(), "회원가입 실패: 상태 코드 ${authResponse.status}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("SignUpFragment", "회원가입 실패: ${response.code()}, 에러 내용: $errorBody")
                        Toast.makeText(requireContext(), "회원가입 실패: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.e("SignUpFragment", "회원가입 요청 오류: ${t.message}")
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
