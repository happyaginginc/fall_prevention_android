//package com.winter.happyaging.analysis
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.winter.happyaging.R
//import com.winter.happyaging.RetrofitClient
//import com.winter.happyaging.databinding.FragmentAnalysisResultBinding
//import com.winter.happyaging.service.AIAnalysisService
//import com.winter.happyaging.ResDTO.ApiResponse
//import com.winter.happyaging.ResDTO.AIAnalysisResponse
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class AnalysisResultFragment : Fragment() {
//
//    private var _binding: FragmentAnalysisResultBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var analysisAdapter: AnalysisAdapter
//    private val analysisList = mutableListOf<AIAnalysisResponse>() // 분석 결과 저장 리스트
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentAnalysisResultBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        fetchAnalysisResults()
//    }
//
//    private fun setupRecyclerView() {
//        analysisAdapter = AnalysisAdapter(analysisList)
//        binding.analysisRecyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = analysisAdapter
//        }
//    }
//
//    private fun fetchAnalysisResults() {
//        val service = RetrofitClient.getInstance(requireContext()).create(AnalysisService::class.java)
//
//        service.getAnalysisResults().enqueue(object : Callback<ApiResponse<AnalysisResult>> {
//            override fun onResponse(
//                call: Call<ApiResponse<AnalysisResult>>, response: Response<ApiResponse<AnalysisResult>>
//            ) {
//                if (response.isSuccessful) {
//                    response.body()?.data?.let {
//                        analysisList.clear()
//                        analysisList.add(it)
//                        analysisAdapter.notifyDataSetChanged()
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "분석 결과를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse<AnalysisResult>>, t: Throwable) {
//                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
