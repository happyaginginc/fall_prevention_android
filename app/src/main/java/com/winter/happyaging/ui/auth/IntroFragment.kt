package com.winter.happyaging.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.R
import com.winter.happyaging.ui.auth.login.LoginFragment
import com.winter.happyaging.ui.auth.register.RegisterFragment
import com.winter.happyaging.ui.main.MainActivity

class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signupButton: Button? = view.findViewById(R.id.signupButton)
        val loginLink: TextView? = view.findViewById(R.id.loginLink)

        // "회원가입 하기"
        signupButton?.setOnClickListener {
            (activity as? MainActivity)?.showFragment(RegisterFragment())
        }

        // "이미 계정이 있으신가요?"
        loginLink?.setOnClickListener {
            (activity as? MainActivity)?.showFragment(LoginFragment())
        }
    }
}