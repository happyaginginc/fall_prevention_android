package com.winter.happyaging.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R
import com.winter.happyaging.login.FindAccountFragment
import com.winter.happyaging.login.SignUpFragment
import com.winter.happyaging.home.HomeActivity

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_login.xml 레이아웃을 inflate
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    // onViewCreated에서 뷰 참조 후 작업
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "이메일/비밀번호 찾기" 텍스트 뷰
        val tvFindEmailPassword = view.findViewById<TextView>(R.id.tvFindEmailPassword)

        // "계정이 없으신가요?" 텍스트뷰
        val tvSignUpLink = view.findViewById<TextView>(R.id.signupLink)

        // "로그인" 버튼 (layout에 해당 버튼이 있다고 가정)
        val btnLogin = view.findViewById<Button>(R.id.loginButton)

        // [1] "이메일/비밀번호 찾기" 클릭 → FindAccountFragment로 교체
        tvFindEmailPassword.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, FindAccountFragment())
                addToBackStack(null)
            }
        }

        // [2] "계정이 없으신가요?" 클릭 → SignUpFragment로 교체
        tvSignUpLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, SignUpFragment())
                addToBackStack(null) // 뒤로가기 시 이전(LoginFragment) 화면 복귀
            }
        }

        // [3] "로그인" 버튼 클릭 시 → 로그인 검증 후, HomeActivity로 이동
        btnLogin.setOnClickListener {
            // TODO: 실제 로그인 검증(서버 통신 등)
            val loginSuccess = true  // 여기선 임시로 성공 처리

            if (loginSuccess) {

//                startActivity(Intent(this, HomeActivity::class.java))
//                finish() // 로그인 성공 시
                // HomeActivity로 이동
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                // 로그인 화면 액티비티(MainActivity)를 닫아, 뒤로가기 시 로그인 화면으로 돌아가지 않게 처리
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
