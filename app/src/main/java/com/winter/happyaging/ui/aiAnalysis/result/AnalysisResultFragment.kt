package com.winter.happyaging.ui.aiAnalysis.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomAIPrompt
import com.winter.happyaging.databinding.FragmentAnalysisResultBinding
import com.winter.happyaging.network.SeniorManager
import com.winter.happyaging.ui.aiAnalysis.result.adapter.AnalysisAdapter
import com.winter.happyaging.ui.home.senior.ManageSeniorActivity

class AnalysisResultFragment : Fragment() {

    companion object {
        private const val ANALYSIS_DATA_PREFS = "AnalysisData"
        private const val ANALYSIS_RESULT_KEY = "analysisResult"
    }

    private var _binding: FragmentAnalysisResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var analysisAdapter: AnalysisAdapter
    private var analysisList: List<RoomAIPrompt> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalysisResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.btnBack)?.visibility = View.GONE
        binding.header.tvHeader.text = "낙상 위험 분석 결과지"

        setupRecyclerView()
        loadAnalysisResults()
        setupSystemBackPressedHandler()
        setupBackButtonClick()

        binding.confirmButton.setOnClickListener {
            val seniorId = getStoredSeniorId()
            SeniorManager.saveSeniorData(requireContext(), seniorId, "홍길동", "서울시 강남구", 1950)
            val intent = Intent(requireContext(), ManageSeniorActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun getStoredSeniorId(): Long {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getLong("seniorId", -1L)
    }

    private fun setupRecyclerView() {
        analysisAdapter = AnalysisAdapter(emptyList())
        binding.analysisRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.analysisRecyclerView.adapter = analysisAdapter
    }

    private fun loadAnalysisResults() {
        val json = arguments?.getString("analysisResultJson") ?:
        requireContext().getSharedPreferences(ANALYSIS_DATA_PREFS, Context.MODE_PRIVATE)
            .getString(ANALYSIS_RESULT_KEY, null)
        if (json != null) {
            val response = Gson().fromJson(json, AiAnalysisResponse::class.java)
            analysisList = response.data.roomAIPrompts
            analysisAdapter.updateData(analysisList)
        } else {
            Toast.makeText(requireContext(), "저장된 분석 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSystemBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
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