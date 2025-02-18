package com.winter.happyaging.ui.aiAnalysis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomAIPrompt
import com.winter.happyaging.databinding.FragmentAnalysisResultBinding
import com.winter.happyaging.ui.aiAnalysis.adapter.AnalysisAdapter

class AnalysisResultFragment : Fragment() {

    companion object {
        private const val TAG = "AnalysisResultFragment"
        private const val ANALYSIS_DATA_PREFS = "AnalysisData"
        private const val ANALYSIS_RESULT_KEY = "analysisResult"
    }

    private var _binding: FragmentAnalysisResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var analysisAdapter: AnalysisAdapter
    private var analysisList: List<RoomAIPrompt> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "낙상 위험 분석 결과지"

        setupRecyclerView()
        loadAnalysisResults()
        setupSystemBackPressedHandler()
        setupBackButtonClick()
    }

    private fun setupRecyclerView() {
        analysisAdapter = AnalysisAdapter(emptyList())
        binding.analysisRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = analysisAdapter
        }
    }

    private fun loadAnalysisResults() {
        val analysisResultJson = arguments?.getString("analysisResultJson")
        if (analysisResultJson != null) {
            val response = Gson().fromJson(analysisResultJson, AiAnalysisResponse::class.java)
            analysisList = response.data.roomAIPrompts
            analysisAdapter.updateData(analysisList)
        } else {
            // 기존 저장된 결과 불러오기
            val sharedPreferences = requireContext().getSharedPreferences(ANALYSIS_DATA_PREFS, Context.MODE_PRIVATE)
            val json = sharedPreferences.getString(ANALYSIS_RESULT_KEY, null)

            if (json != null) {
                val response = Gson().fromJson(json, AiAnalysisResponse::class.java)
                Log.d(TAG, "불러온 분석 결과 JSON: $json")
                analysisList = response.data.roomAIPrompts
                analysisAdapter.updateData(analysisList)
            } else {
                Toast.makeText(requireContext(), "저장된 분석 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSystemBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        )
    }

    private fun setupBackButtonClick() {
        binding.header.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}