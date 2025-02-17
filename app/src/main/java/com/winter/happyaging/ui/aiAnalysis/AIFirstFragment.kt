package com.winter.happyaging.ui.aiAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.winter.happyaging.ImageManager
import com.winter.happyaging.R
import com.winter.happyaging.databinding.FragmentAiFirstBinding

class AIFirstFragment : Fragment() {
    private var _binding: FragmentAiFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageManager = ImageManager(requireContext())
        imageManager.clearLocalImageData()

        binding.btnStartAnalysis.setOnClickListener {
            view.findNavController().navigate(R.id.action_AIFirstFragment_to_AISecondFragment)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        setupBackPressedHandler()
    }

    private fun setupBackPressedHandler() {
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
