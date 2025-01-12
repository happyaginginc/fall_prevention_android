package com.winter.happyaging.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R

class SignUpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // fragment_sign_up.xml 인플레이션
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "내용 보기" 텍스트뷰 찾아오기
        val tvTermsDetail = view.findViewById<TextView>(R.id.tvTermsDetail)

        val etName = view.findViewById<EditText>(R.id.etName)
        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)

        // 클릭 시 TermsFragment로 이동
        tvTermsDetail.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, TermsFragment())
                addToBackStack(null)
            }
        }

        btnSignUp.setOnClickListener {
            // 예: 회원가입 로직(서버 통신 등) 처리 후 성공 시 아래 동작
            val name = etName.text.toString().trim()

            // SignUpCompleteFragment를 인스턴스화할 때 이름을 전달
            val completeFragment = SignUpCompleteFragment.newInstance(name)

            // 프래그먼트 전환
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, completeFragment)
                addToBackStack(null)
            }
        }
    }
}

