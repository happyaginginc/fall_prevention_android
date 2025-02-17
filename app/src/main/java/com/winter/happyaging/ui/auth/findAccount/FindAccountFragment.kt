package com.winter.happyaging.ui.auth.findAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.winter.happyaging.R
import com.winter.happyaging.databinding.FragmentFindAccountBinding

class FindAccountFragment : Fragment() {

    private var _binding: FragmentFindAccountBinding? = null
    private val binding get() = _binding!!

    // 임시 Mock 사용자 데이터
    private val mockUserData = listOf(
        User("홍길동", "010-0000-0000", "minseo040727@naver.com"),
        User("김철수", "010-1234-5678", "abc@google.com")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFindAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnFindEmail.setOnClickListener {
            val name = binding.edtFindEmailName.text.toString().trim()
            val phone = binding.edtFindEmailPhone.text.toString().trim()
            val foundEmail = findEmailByNameAndPhone(name, phone)
            val (title, message, tag) = if (foundEmail != null) {
                Triple(getString(R.string.email_find_success_title),
                    getString(R.string.email_find_success_message, name, foundEmail),
                    "EmailFindSuccess")
            } else {
                Triple(getString(R.string.email_find_fail_title),
                    getString(R.string.email_find_fail_message),
                    "EmailFindFail")
            }
            FindAccountDialog.newInstance(title, message, foundEmail != null)
                .show(parentFragmentManager, tag)
        }

        binding.btnFindPw.setOnClickListener {
            val email = binding.edtFindPwEmail.text.toString().trim()
            val isSent = sendTempPasswordToEmail(email)
            val (title, message, tag) = if (isSent) {
                Triple(getString(R.string.password_send_success_title),
                    getString(R.string.password_send_success_message, email),
                    "PwSendSuccess")
            } else {
                Triple(getString(R.string.password_send_fail_title),
                    getString(R.string.password_send_fail_message),
                    "PwSendFail")
            }
            FindAccountDialog.newInstance(title, message, isSent).show(parentFragmentManager, tag)
        }
    }

    private fun findEmailByNameAndPhone(name: String, phone: String): String? =
        mockUserData.find { it.name == name && it.phone == phone }?.email

    private fun sendTempPasswordToEmail(email: String): Boolean =
        mockUserData.any { it.email.equals(email, ignoreCase = true) }

    data class User(val name: String, val phone: String, val email: String)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}