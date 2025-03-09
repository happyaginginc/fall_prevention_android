package com.winter.happyaging.ui.aiAnalysis.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.winter.happyaging.R
import com.winter.happyaging.databinding.FragmentAiIntroBinding
import com.winter.happyaging.network.ImageManager

class AIIntroFragment : Fragment() {

    private var _binding: FragmentAiIntroBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageManager: ImageManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageManager = ImageManager(requireContext())
        try {
            imageManager.clearLocalImageData()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "이미지 데이터를 초기화하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.btnStartAnalysis.setOnClickListener {
            try {
                view.findNavController().navigate(R.id.action_AIIntroFragment_to_AIBathroomFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "분석 시작 화면으로 이동할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        view.findViewById<TextView>(R.id.tvHeader)?.text = "낙상 위험 분석 시작하기"
        binding.header.btnBack.setOnClickListener { requireActivity().finish() }
        setupSystemBackPressedHandler()
    }

    private fun setupSystemBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}