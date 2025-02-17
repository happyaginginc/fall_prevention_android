package com.winter.happyaging.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R
import com.winter.happyaging.ui.auth.login.LoginFragment

/**
 * 회원가입 완료 화면
 */
class RegisterCompleteFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"

        fun newInstance(name: String): RegisterCompleteFragment {
            return RegisterCompleteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString(ARG_NAME, "") ?: ""
        val tvUserCongrats: TextView = view.findViewById(R.id.congratulationsTextView)
        val btnGoLogin: Button = view.findViewById(R.id.loginButton)

        tvUserCongrats.text = getString(R.string.register_congrats_message, name)

        btnGoLogin.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, LoginFragment())
                addToBackStack(null)
            }
        }
    }
}