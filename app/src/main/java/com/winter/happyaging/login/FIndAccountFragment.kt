package com.winter.happyaging.login

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.winter.happyaging.R

class FindAccountFragment : Fragment() {

    private lateinit var edtFindEmailName: EditText
    private lateinit var edtFindEmailPhone: EditText
    private lateinit var btnFindEmail: Button

    private lateinit var edtFindPwEmail: EditText
    private lateinit var btnFindPw: Button

    // 예: 실제로는 Repository/Retrofit/Room 등을 주입받아 사용
    // 여기서는 임시 Map으로 사용자 정보를 저장 (name+phone -> email)
    private val mockUserData = listOf(
        User(name="홍길동", phone="010-0000-0000", email="minseo040727@naver.com"),
        User(name="김철수", phone="010-1234-5678", email="abc@google.com")
        // ...
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

        // [1] 이메일 찾기 버튼
        btnFindEmail.setOnClickListener {
            val name = edtFindEmailName.text.toString().trim()
            val phone = edtFindEmailPhone.text.toString().trim()

            // Mock 함수로 이메일 조회
            val foundEmail = findEmailByNameAndPhone(name, phone)

            if (foundEmail != null) {
                // 성공 다이얼로그
                val dialog = FindAccountDialog.newInstance(
                    title = "이메일 찾기 완료",
                    message = "$name 님의 가입 이메일은\n$foundEmail 입니다.",
                    success = true
                )
                dialog.show(parentFragmentManager, "EmailFindSuccess")
            } else {
                // 실패 다이얼로그
                val dialog = FindAccountDialog.newInstance(
                    title = "이메일 찾기 실패",
                    message = "$name 님에 대한 정보를 찾을 수 없습니다.\n이름 또는 전화번호를 다시 확인해주세요.",
                    success = false
                )
                dialog.show(parentFragmentManager, "EmailFindFail")
            }
        }

        // [2] 비밀번호 찾기(발급) 버튼
        btnFindPw.setOnClickListener {
            val email = edtFindPwEmail.text.toString().trim()

            // Mock 함수로 비밀번호 발급 시도
            val success = sendTempPasswordToEmail(email)

            if (success) {
                // 성공
                val dialog = FindAccountDialog.newInstance(
                    title = "비밀번호 발송 완료",
                    message = "$email\n비밀번호가 메일로 발송되었습니다.",
                    success = true
                )
                dialog.show(parentFragmentManager, "PwSendSuccess")
            } else {
                // 실패
                val dialog = FindAccountDialog.newInstance(
                    title = "비밀번호 발송 실패",
                    message = "해당 이메일에 대한 정보를 찾을 수 없습니다.",
                    success = false
                )
                dialog.show(parentFragmentManager, "PwSendFail")
            }
        }
    }

    /**
     * (Mock) DB/서버에서 name + phone 으로 검색하여 email 리턴
     */
    private fun findEmailByNameAndPhone(name: String, phone: String): String? {
        return mockUserData.find {
            it.name == name && it.phone == phone
        }?.email
    }

    /**
     * (Mock) DB/서버에서 email 이 존재하면 임시비번 발송 → true, 없으면 false
     */
    private fun sendTempPasswordToEmail(email: String): Boolean {
        // 실제론 Retrofit 등으로 서버에 요청한 뒤 성공 시 true, 실패 시 false
        val user = mockUserData.find { it.email.equals(email, ignoreCase = true) }
        return user != null
    }

    data class User(
        val name: String,
        val phone: String,
        val email: String
    )
}
