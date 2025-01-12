package com.winter.happyaging.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.R

class SignUpCompleteFragment : Fragment() {

    // 인자로 전달된 사용자 이름을 꺼내기 위한 키
    companion object {
        private const val ARG_NAME = "arg_name"

        // newInstance 메서드를 통해 name 인자를 번들에 담아 프래그먼트에 전달
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

        // 번들로부터 이름 가져오기 (디폴트값은 빈 문자열)
        val name = arguments?.getString(ARG_NAME, "") ?: ""

        // "홍길동 님, 가입을 축하합니다!" 식으로 표현하기 위해 텍스트뷰 설정
        val tvUserCongrats = view.findViewById<TextView>(R.id.congratulationsTextView)
        tvUserCongrats.text = "${name} 님, 가입을 축하합니다!"

        // 로그인 버튼 눌렀을 때, 로그인 화면으로 이동(예: LoginFragment)
        val btnGoLogin = view.findViewById<Button>(R.id.loginButton)
        btnGoLogin.setOnClickListener {
            // 로그인 화면으로 이동하는 코드 예시
            parentFragmentManager.popBackStack()
            // 또는 replace로 이동할 수도 있음
            // parentFragmentManager.commit {
            //     replace(R.id.fragmentContainer, LoginFragment())
            //     // 필요하다면 addToBackStack(null)
            // }
        }
    }
}
