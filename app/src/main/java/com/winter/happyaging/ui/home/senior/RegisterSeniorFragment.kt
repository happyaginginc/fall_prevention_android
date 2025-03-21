// app/src/main/java/com/winter/happyaging/ui/home/senior/RegisterSeniorFragment.kt
package com.winter.happyaging.ui.home.senior

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.winter.happyaging.R
import com.winter.happyaging.data.senior.model.SeniorCreateRequest
import com.winter.happyaging.data.senior.model.SeniorCreateResponse
import com.winter.happyaging.data.senior.service.SeniorService
import com.winter.happyaging.databinding.FragmentRegisterSeniorBinding
import com.winter.happyaging.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterSeniorFragment : Fragment() {

    private var _binding: FragmentRegisterSeniorBinding? = null
    private val binding get() = _binding!!

    private val phoneRegex = Regex("^010-\\d{4}-\\d{4}\$")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterSeniorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sexOptions = listOf("남자", "여자")
        val relationshipOptions = listOf("본인", "가족", "기타 (친구, 지인 등)")
        binding.spinnerSex.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexOptions)
        binding.spinnerRelationship.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, relationshipOptions)

        val sexMapping = mapOf("남자" to "MALE", "여자" to "FEMALE")
        val relationshipMapping = mapOf("본인" to "SELF", "가족" to "FAMILY", "기타 (친구, 지인 등)" to "ETC")

        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "시니어 관리"

        binding.header.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateName() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        binding.edtAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateAddress() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        binding.edtAddressDetail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateAddressDetail() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        binding.edtBirthYear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateBirthYear() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validatePhone() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        binding.fabRegister.setOnClickListener {
            val isNameValid = validateName()
            val isAddressValid = validateAddress()
            val isAddressDetailValid = validateAddressDetail()
            val isBirthYearValid = validateBirthYear()
            val isPhoneValid = validatePhone()

            if (!isNameValid || !isAddressValid || !isAddressDetailValid || !isBirthYearValid || !isPhoneValid) {
                Toast.makeText(requireContext(), "모든 필수 정보를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.scrollViewRegister.post { binding.scrollViewRegister.smoothScrollTo(0, binding.edtName.top) }
                return@setOnClickListener
            }

            val name = binding.edtName.text.toString().trim()
            val address = binding.edtAddress.text.toString().trim()
            val addressDetail = binding.edtAddressDetail.text.toString().trim()
            val birthYear = try {
                binding.edtBirthYear.text.toString().trim().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "출생년도 입력이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val phoneNumberInput = binding.edtPhone.text.toString().trim()
            val memo = binding.edtMemo.text.toString().trim()

            val selectedSex = binding.spinnerSex.selectedItem.toString().trim()
            val selectedRelationship = binding.spinnerRelationship.selectedItem.toString().trim()
            val internalSex = sexMapping[selectedSex] ?: selectedSex
            val internalRelationship = relationshipMapping[selectedRelationship] ?: selectedRelationship

            val formattedPhoneNumber = formatPhoneNumber(phoneNumberInput)

            val requestData = SeniorCreateRequest(
                name = name,
                address = "$address $addressDetail",
                birthYear = birthYear,
                sex = internalSex,
                phoneNumber = formattedPhoneNumber,
                relationship = internalRelationship,
                memo = memo
            )

            sendSeniorRequest(requestData)
        }
    }

    private fun validateName(): Boolean {
        val name = binding.edtName.text.toString().trim()
        return if (name.isEmpty()) {
            binding.tvNameError.visibility = View.VISIBLE
            binding.tvNameError.text = "이름은 필수값입니다."
            false
        } else {
            binding.tvNameError.visibility = View.GONE
            true
        }
    }

    private fun validateAddress(): Boolean {
        val address = binding.edtAddress.text.toString().trim()
        return if (address.isEmpty()) {
            binding.tvAddressError.visibility = View.VISIBLE
            binding.tvAddressError.text = "거주지를 입력해주세요."
            false
        } else {
            binding.tvAddressError.visibility = View.GONE
            true
        }
    }

    private fun validateAddressDetail(): Boolean {
        val addressDetail = binding.edtAddressDetail.text.toString().trim()
        return if (addressDetail.isEmpty()) {
            binding.tvAddressDetailError.visibility = View.VISIBLE
            binding.tvAddressDetailError.text = "상세 주소를 입력해주세요."
            false
        } else {
            binding.tvAddressDetailError.visibility = View.GONE
            true
        }
    }

    private fun validateBirthYear(): Boolean {
        val birthYearText = binding.edtBirthYear.text.toString().trim()
        val birthYear = birthYearText.toIntOrNull()
        return if (birthYearText.isEmpty() || birthYear == null || birthYear < 1900 || birthYear > 2100) {
            binding.tvBirthYearError.visibility = View.VISIBLE
            binding.tvBirthYearError.text = "올바른 출생년도를 입력해주세요 (예: 2000)"
            false
        } else {
            binding.tvBirthYearError.visibility = View.GONE
            true
        }
    }

    private fun validatePhone(): Boolean {
        val phone = binding.edtPhone.text.toString().trim()
        return if (phone.isEmpty() || !phoneRegex.matches(phone)) {
            binding.tvPhoneError.visibility = View.VISIBLE
            binding.tvPhoneError.text = "휴대폰 번호 양식이 올바르지 않습니다. 예) 010-1234-5678"
            false
        } else {
            binding.tvPhoneError.visibility = View.GONE
            true
        }
    }

    private fun formatPhoneNumber(input: String): String {
        val cleaned = input.replace(Regex("[^0-9]"), "")
        return when (cleaned.length) {
            10 -> cleaned.replace(Regex("(\\d{3})(\\d{3})(\\d{4})"), "$1-$2-$3")
            11 -> cleaned.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
            else -> input
        }
    }

    private fun sendSeniorRequest(request: SeniorCreateRequest) {
        val seniorService = RetrofitClient.getInstance(requireContext()).create(SeniorService::class.java)
        seniorService.registerSenior(request).enqueue(object : Callback<SeniorCreateResponse> {
            override fun onResponse(call: Call<SeniorCreateResponse>, response: Response<SeniorCreateResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "시니어 등록 성공!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.setFragmentResult("refreshSeniorList", Bundle())
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "등록 실패: 서버 오류 ${response.code()}. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<SeniorCreateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류 발생: ${t.localizedMessage}. 인터넷 연결을 확인하고 다시 시도해주세요.", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}