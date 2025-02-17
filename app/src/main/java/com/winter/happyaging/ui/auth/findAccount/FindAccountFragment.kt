package com.winter.happyaging.ui.auth.findAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.winter.happyaging.R

class FindAccountFragment : Fragment() {

    private lateinit var edtFindEmailName: EditText
    private lateinit var edtFindEmailPhone: EditText
    private lateinit var btnFindEmail: Button
    private lateinit var edtFindPwEmail: EditText
    private lateinit var btnFindPw: Button

    // 임시 Mock 사용자 데이터
    private val mockUserData = listOf(
        User("홍길동", "010-0000-0000", "minseo040727@naver.com"),
        User("김철수", "010-1234-5678", "abc@google.com")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtFindEmailName = view.findViewById(R.id.edtFindEmailName)
        edtFindEmailPhone = view.findViewById(R.id.edtFindEmailPhone)
        btnFindEmail = view.findViewById(R.id.btnFindEmail)
        edtFindPwEmail = view.findViewById(R.id.edtFindPwEmail)
        btnFindPw = view.findViewById(R.id.btnFindPw)

        // [1] 이메일 찾기
        btnFindEmail.setOnClickListener {
            val name = edtFindEmailName.text.toString().trim()
            val phone = edtFindEmailPhone.text.toString().trim()

            val foundEmail = findEmailByNameAndPhone(name, phone)
            if (foundEmail != null) {
                FindAccountDialog.newInstance(
                    title = getString(R.string.email_find_success_title),
                    message = getString(R.string.email_find_success_message, name, foundEmail),
                    success = true
                ).show(parentFragmentManager, "EmailFindSuccess")
            } else {
                FindAccountDialog.newInstance(
                    title = getString(R.string.email_find_fail_title),
                    message = getString(R.string.email_find_fail_message),
                    success = false
                ).show(parentFragmentManager, "EmailFindFail")
            }
        }

        // [2] 비밀번호 찾기
        btnFindPw.setOnClickListener {
            val email = edtFindPwEmail.text.toString().trim()

            if (sendTempPasswordToEmail(email)) {
                FindAccountDialog.newInstance(
                    title = getString(R.string.password_send_success_title),
                    message = getString(R.string.password_send_success_message, email),
                    success = true
                ).show(parentFragmentManager, "PwSendSuccess")
            } else {
                FindAccountDialog.newInstance(
                    title = getString(R.string.password_send_fail_title),
                    message = getString(R.string.password_send_fail_message),
                    success = false
                ).show(parentFragmentManager, "PwSendFail")
            }
        }
    }

    // Mock: name+phone으로 email 찾기
    private fun findEmailByNameAndPhone(name: String, phone: String): String? {
        return mockUserData.find { it.name == name && it.phone == phone }?.email
    }

    // Mock: email 존재 시 임시비번 발송 성공(true), 없으면 false
    private fun sendTempPasswordToEmail(email: String): Boolean {
        return mockUserData.any { it.email.equals(email, ignoreCase = true) }
    }

    data class User(val name: String, val phone: String, val email: String)
}