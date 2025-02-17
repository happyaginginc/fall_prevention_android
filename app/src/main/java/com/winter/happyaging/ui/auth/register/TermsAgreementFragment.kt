package com.winter.happyaging.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.winter.happyaging.R

/**
 * 약관 동의 화면
 */
class TermsAgreementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageView = view.findViewById(R.id.btnBack)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)

        // 뒤로가기
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 약관 동의 확인 후 뒤로가기
        btnConfirm.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}