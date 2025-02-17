package com.winter.happyaging.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.winter.happyaging.data.senior.model.SeniorCreateRequest
import com.winter.happyaging.data.senior.model.SeniorCreateResponse
import com.winter.happyaging.data.senior.service.SeniorService
import com.winter.happyaging.databinding.FragmentRegisterSeniorBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterSeniorFragment : Fragment() {

    private var _binding: FragmentRegisterSeniorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterSeniorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sexOptions = listOf("MALE", "FEMALE")
        val relationshipOptions = listOf("SELF", "FAMILY", "ETC")
        binding.spinnerSex.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexOptions)
        binding.spinnerRelationship.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, relationshipOptions)

        binding.fabRegister.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val address = binding.edtAddress.text.toString().trim()
            val birthYearText = binding.edtBirthYear.text.toString().trim()
            val phoneNumber = binding.edtPhone.text.toString().trim()
            val memo = binding.edtMemo.text.toString().trim()
            val sex = binding.spinnerSex.selectedItem.toString().trim()
            val relationship = binding.spinnerRelationship.selectedItem.toString().trim()

            if (name.isEmpty() || address.isEmpty() || birthYearText.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val birthYear = birthYearText.toIntOrNull()
            if (birthYear == null || birthYear < 1900 || birthYear > 2100) {
                Toast.makeText(requireContext(), "올바른 출생년도를 입력해주세요 (예: 2000)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
            val requestData = SeniorCreateRequest(
                name = name,
                address = address,
                birthYear = birthYear,
                sex = sex,
                phoneNumber = formattedPhoneNumber,
                relationship = relationship,
                memo = memo
            )
            sendSeniorRequest(requestData)
        }
    }

    private fun sendSeniorRequest(request: SeniorCreateRequest) {
        val seniorService = RetrofitClient.getInstance(requireContext()).create(SeniorService::class.java)
        val tokenManager = TokenManager(requireContext())
        // 로그를 통해 토큰 확인 가능
        seniorService.registerSenior(request).enqueue(object : Callback<SeniorCreateResponse> {
            override fun onResponse(call: Call<SeniorCreateResponse>, response: Response<SeniorCreateResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "시니어 등록 성공!", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                    (requireActivity() as? HomeActivity)?.fetchSeniorData()
                } else {
                    Toast.makeText(requireContext(), "등록 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SeniorCreateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatPhoneNumber(input: String): String {
        val cleaned = input.replace(Regex("[^0-9]"), "")
        return when (cleaned.length) {
            10 -> cleaned.replace(Regex("(\\d{3})(\\d{3})(\\d{4})"), "$1-$2-$3")
            11 -> cleaned.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
            else -> input
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}