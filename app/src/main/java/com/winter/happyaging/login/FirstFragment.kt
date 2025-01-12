package com.winter.happyaging.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.MainActivity
import com.winter.happyaging.R

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "회원가입 하기" 버튼 클릭 시 회원가입 프래그먼트로 이동
        view.findViewById<Button>(R.id.signupButton).setOnClickListener {
            (activity as? MainActivity)?.showFragment(SignUpFragment())
        }

        // "이미 계정이 있으신가요?" 클릭 시 로그인 프래그먼트로 이동
        view.findViewById<TextView>(R.id.loginLink).setOnClickListener {
            (activity as? MainActivity)?.showFragment(LoginFragment())
        }
    }
}
