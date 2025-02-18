package com.winter.happyaging.ui.home.senior

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.data.senior.service.SeniorService
import com.winter.happyaging.databinding.FragmentSeniorListBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import com.winter.happyaging.ui.home.senior.adapter.SeniorAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeniorListFragment : Fragment() {

    private var _binding: FragmentSeniorListBinding? = null
    private val binding get() = _binding!!

    // 전체 시니어 리스트를 저장할 변수
    private var fullSeniorList: List<SeniorReadResponse> = emptyList()
    private val seniorAdapter = SeniorAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSeniorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.btnBack)?.visibility = View.GONE
        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "낙상 위험 분석 시작하기"

        // RecyclerView 설정
        with(binding.recyclerSenior) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = seniorAdapter
        }

        // 검색어 입력 시 텍스트 변화 감지
        binding.edtSearchSenior.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterSeniorList(query)
        }

        // FragmentResult를 수신하여 데이터 갱신
        parentFragmentManager.setFragmentResultListener("refreshSeniorList", this) { _, _ ->
            fetchSeniorData()
        }

        fetchSeniorData()

        // 시니어 등록 버튼 클릭
        binding.btnRegisterSenior.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterSeniorFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    // 서버에서 시니어 데이터를 가져와 전체 리스트와 어댑터 업데이트
    private fun fetchSeniorData() {
        val seniorService = RetrofitClient.getInstance(requireContext()).create(SeniorService::class.java)
        seniorService.getSeniorAllList().enqueue(object : Callback<ApiResponse<List<SeniorReadResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SeniorReadResponse>>>,
                response: Response<ApiResponse<List<SeniorReadResponse>>>,
            ) {
                if (response.isSuccessful) {
                    fullSeniorList = response.body()?.data.orEmpty()
                    Log.d("SeniorListFragment", "받아온 시니어 리스트: $fullSeniorList")
                    // 현재 검색어가 있을 경우 필터링 적용
                    val currentQuery = binding.edtSearchSenior.text.toString().trim()
                    filterSeniorList(currentQuery)
                } else {
                    Log.e("SeniorListFragment", "API 호출 실패 - 상태 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<SeniorReadResponse>>>, t: Throwable) {
                Log.e("SeniorListFragment", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 검색어에 따라 리스트 필터링 및 어댑터 업데이트
    private fun filterSeniorList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullSeniorList
        } else {
            fullSeniorList.filter { it.name.contains(query, ignoreCase = true) }
        }
        seniorAdapter.updateData(filteredList)
    }

    override fun onResume() {
        super.onResume()
        Log.d("SeniorListFragment", "onResume 호출됨 - fetchSeniorData() 실행")
        fetchSeniorData() // 화면이 다시 보일 때마다 데이터 갱신
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}