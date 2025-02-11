package com.winter.happyaging.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.winter.happyaging.R
import com.winter.happyaging.ResDTO.SeniorResponse
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.service.SeniorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterSeniorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_senior, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edtName = view.findViewById<EditText>(R.id.edtName)
        val edtAddress = view.findViewById<EditText>(R.id.edtAddress)
        val edtBirthYear = view.findViewById<EditText>(R.id.edtBirthYear)
        val edtPhoneNumber = view.findViewById<EditText>(R.id.edtPhone)
        val edtMemo = view.findViewById<EditText>(R.id.edtMemo)

        val spinnerSex = view.findViewById<Spinner>(R.id.spinnerSex)
        val spinnerRelationship = view.findViewById<Spinner>(R.id.spinnerRelationship)
        val fabRegister = view.findViewById<ExtendedFloatingActionButton>(R.id.fabRegister)

        val sexOptions = listOf("MALE", "FEMALE")
        val relationshipOptions = listOf("SELF", "FAMILY", "ETC")

        val sexAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexOptions)
        spinnerSex.adapter = sexAdapter

        val relationshipAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, relationshipOptions)
        spinnerRelationship.adapter = relationshipAdapter

        // 등록 버튼 클릭 이벤트
        fabRegister.setOnClickListener {
            val name = edtName.text.toString().trim()
            val address = edtAddress.text.toString().trim()
            val birthYearText = edtBirthYear.text.toString().trim()
            val phoneNumber = edtPhoneNumber.text.toString().trim()
            val memo = edtMemo.text.toString().trim()

            val formattedPhoneNumber = formatPhoneNumber(phoneNumber)

            val sex = spinnerSex.selectedItem?.toString()?.trim() ?: "MALE"
            val relationship = spinnerRelationship.selectedItem?.toString()?.trim() ?: "SELF"

            // 🚨 필수 값 검증 (이름, 주소, 출생년도, 성별, 관계, 전화번호)
            if (name.isEmpty() || address.isEmpty() || birthYearText.isEmpty() || phoneNumber.isEmpty() || sex.isEmpty() || relationship.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🚨 출생년도 숫자 변환 검증
            val birthYear = birthYearText.toIntOrNull()
            if (birthYear == null || birthYear < 1900 || birthYear > 2100) {
                Toast.makeText(requireContext(), "올바른 출생년도를 입력해주세요 (예: 2000)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("RegisterSeniorFragment", "보내는 데이터 - name: $name, address: $address, birthYear: $birthYear, sex: $sex, phoneNumber: $phoneNumber, relationship: $relationship, memo: $memo")

            // SeniorRequest 객체 생성 (Sex & Relationship을 String으로 저장)
            val data = SeniorData(
                name = name,
                address = address,
                birthYear = birthYear,
                sex = sex, // String으로 저장
                phoneNumber = formattedPhoneNumber,
                relationship = relationship, // String으로 저장
                memo = memo
            )

            sendSeniorRequest(data)
        }
    }

    private fun sendSeniorRequest(request: SeniorData) {
        val seniorService = RetrofitClient.getInstance(requireContext()).create(SeniorService::class.java)
        seniorService.registerSenior(request).enqueue(object : Callback<SeniorResponse> {
            override fun onResponse(call: Call<SeniorResponse>, response: Response<SeniorResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "시니어 등록 성공!", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()

                    // 🚀 데이터 갱신 요청
                    (requireActivity() as? HomeActivity)?.fetchSeniorData()
                } else {
                    Toast.makeText(requireContext(), "등록 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SeniorResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatPhoneNumber(input: String): String {
        val cleaned = input.replace(Regex("[^0-9]"), "") // 숫자만 남김
        return when {
            cleaned.length == 10 -> cleaned.replace(Regex("(\\d{3})(\\d{3})(\\d{4})"), "$1-$2-$3")
            cleaned.length == 11 -> cleaned.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
            else -> input // 올바르지 않은 경우 원본 유지
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("RegisterSeniorFragment", "🛑 RegisterSeniorFragment 제거됨")
    }

}