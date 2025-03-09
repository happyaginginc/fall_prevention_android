package com.winter.happyaging.ui.aiAnalysis.record

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.repository.AiAnalysisRepository
import com.winter.happyaging.databinding.FragmentAnalysisRecordListBinding
import com.winter.happyaging.ui.aiAnalysis.record.adapter.RecordDateAdapter
import com.winter.happyaging.ui.aiAnalysis.result.AnalysisResultFragment

class AnalysisRecordListFragment : Fragment(R.layout.fragment_analysis_record_list) {

    private var _binding: FragmentAnalysisRecordListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recordDateAdapter: RecordDateAdapter
    private var dateList: List<String> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAnalysisRecordListBinding.bind(view)

        view.findViewById<TextView>(R.id.tvHeader)?.text = "낙상 위험 분석 기록"
        binding.header.btnBack.setOnClickListener { requireActivity().finish() }

        setupRecyclerView()
        fetchRecordDates()
    }

    private fun setupRecyclerView() {
        recordDateAdapter = RecordDateAdapter(dateList) { selectedDate ->
            fetchAnalysisByDate(selectedDate)
        }
        binding.recordDatesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recordDatesRecyclerView.adapter = recordDateAdapter
    }

    private fun fetchRecordDates() {
        try {
            val seniorId = getStoredSeniorId()
            if (seniorId == -1L) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return
            }
            AiAnalysisRepository.getRecordDates(requireContext(), seniorId,
                onSuccess = { dates ->
                    dateList = dates
                    recordDateAdapter.updateData(dateList)
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "기록 목록 로드 실패: $error", Toast.LENGTH_SHORT).show()
                })
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "기록 목록을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAnalysisByDate(date: String) {
        try {
            val seniorId = getStoredSeniorId()
            if (seniorId == -1L) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return
            }
            binding.progressBar.visibility = View.VISIBLE
            AiAnalysisRepository.getAnalysisByDate(requireContext(), seniorId, date,
                onSuccess = { response ->
                    val json = Gson().toJson(response)
                    val bundle = Bundle().apply { putString("analysisResultJson", json) }
                    binding.progressBar.visibility = View.GONE
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AnalysisResultFragment::class.java, bundle)
                        .addToBackStack(null)
                        .commit()
                },
                onFailure = { error ->
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "분석 결과 로드 실패: $error", Toast.LENGTH_SHORT).show()
                })
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "분석 결과를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getStoredSeniorId(): Long {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getLong("seniorId", -1L)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}