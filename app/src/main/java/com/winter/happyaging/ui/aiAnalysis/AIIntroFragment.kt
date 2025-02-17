package com.winter.happyaging.ui.aiAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.winter.happyaging.R
import com.winter.happyaging.databinding.FragmentAiFirstBinding
import com.winter.happyaging.network.ImageManager

class AIIntroFragment : Fragment() {

    private var _binding: FragmentAiFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageManager: ImageManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageManager = ImageManager(requireContext())
        imageManager.clearLocalImageData()

        binding.btnStartAnalysis.setOnClickListener {
            view.findNavController().navigate(R.id.action_AIIntroFragment_to_AILivingRoomFragment)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        setupSystemBackPressedHandler()
    }

    private fun setupSystemBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}