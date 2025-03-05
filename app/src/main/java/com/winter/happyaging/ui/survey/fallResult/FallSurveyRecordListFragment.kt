package com.winter.happyaging.ui.survey.fallResult

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.survey.model.SurveyResultData
import com.winter.happyaging.data.survey.service.SurveyService
import com.winter.happyaging.databinding.FragmentFallSurveyRecordListBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FallSurveyRecordListFragment : Fragment(R.layout.fragment_fall_survey_record_list) {

    private var _binding: FragmentFallSurveyRecordListBinding? = null
    private val binding get() = _binding!!

    private lateinit var surveyRecordAdapter: FallSurveyRecordAdapter
    private var surveyList: List<SurveyResultData> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFallSurveyRecordListBinding.bind(view)

        // 헤더 세팅
        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "낙상 위험등급 결과 목록"
        binding.header.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        setupRecyclerView()

        // Senior ID 가져오기
        val seniorId = arguments?.getLong("seniorId", -1L)
            ?: requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getLong("seniorId", -1L)

        if (seniorId == -1L) {
            Toast.makeText(requireContext(), "시니어 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        fetchAllSurveyResults(seniorId)
    }

    private fun setupRecyclerView() {
        surveyRecordAdapter = FallSurveyRecordAdapter { clickedSurvey ->
            // 아이템 클릭 시 팝업을 띄운다.
            FallSurveyRecordDialog.newInstance(clickedSurvey)
                .show(parentFragmentManager, "FallSurveyRecordDialog")
        }

        binding.recyclerFallSurvey.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = surveyRecordAdapter
        }
    }

    private fun fetchAllSurveyResults(seniorId: Long) {
        binding.progressBar.visibility = View.VISIBLE

        val service = RetrofitClient.getInstance(requireContext()).create(SurveyService::class.java)
        service.getAllSurveyResults(seniorId).enqueue(object : Callback<ApiResponse<List<SurveyResultData>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SurveyResultData>>>,
                response: Response<ApiResponse<List<SurveyResultData>>>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.status == 200) {
                    surveyList = response.body()?.data ?: emptyList()
                    surveyRecordAdapter.updateData(surveyList)
                } else {
                    Toast.makeText(requireContext(), "조회 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<SurveyResultData>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
