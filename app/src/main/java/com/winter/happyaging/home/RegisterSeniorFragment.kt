package com.winter.happyaging.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.winter.happyaging.R

class RegisterSeniorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_senior, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 플로팅 "등록하기" 버튼
        val fabRegister = view.findViewById<ExtendedFloatingActionButton>(R.id.fabRegister)
        fabRegister.setOnClickListener {
            // TODO: 입력값을 서버/DB에 저장 후 처리
            // 여기서는 예시로 Toast만 표시
            Toast.makeText(requireContext(), "시니어 정보 등록 완료!", Toast.LENGTH_SHORT).show()

            // 등록 완료 후, 이전 화면으로 돌아가거나 다른 화면 전환 등 원하는 로직
            // requireActivity().onBackPressed() 등
        }
    }
}
