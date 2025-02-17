package com.winter.happyaging.ui.aiAnalysis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomAIPrompt
import com.winter.happyaging.databinding.FragmentAnalysisResultBinding
import com.winter.happyaging.ui.aiAnalysis.adapter.AnalysisAdapter

class AnalysisResultFragment : Fragment() {
    private var _binding: FragmentAnalysisResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var analysisAdapter: AnalysisAdapter
    private var analysisList: List<RoomAIPrompt> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadAnalysisResults()
    }

    private fun setupRecyclerView() {
        analysisAdapter = AnalysisAdapter(emptyList())
        binding.analysisRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = analysisAdapter
        }
    }

    private fun loadAnalysisResults() {
        val sharedPreferences = requireContext().getSharedPreferences("AnalysisData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("analysisResult", null)

        if (json != null) {
            val response = gson.fromJson(json, AiAnalysisResponse::class.java)
            Log.d("AnalysisResultFragment", "불러온 분석 결과 JSON: $json")
            analysisList = response.data.roomAIPrompts
            analysisAdapter.updateData(analysisList)
        } else {
            Toast.makeText(requireContext(), "저장된 분석 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
