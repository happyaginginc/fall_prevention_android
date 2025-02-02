package com.winter.happyaging.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R

class SignUpCompleteFragment : Fragment() {

    companion object { // 사용자 이름
        private const val ARG_NAME = "arg_name"

        fun newInstance(name: String): SignUpCompleteFragment {
            val fragment = SignUpCompleteFragment()
            val bundle = Bundle().apply {
                putString(ARG_NAME, name)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString(ARG_NAME, "") ?: ""

        val tvUserCongrats = view.findViewById<TextView>(R.id.congratulationsTextView)
        tvUserCongrats.text = "${name} 님, 가입을 축하합니다!"

        val btnGoLogin = view.findViewById<Button>(R.id.loginButton)
        btnGoLogin.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, LoginFragment()) // 로그인 화면으로 이동
                addToBackStack(null) // 뒤로 가기 버튼 눌렀을 때 이전 화면으로 돌아갈 수 있도록 설정
            }
        }
    }
}
