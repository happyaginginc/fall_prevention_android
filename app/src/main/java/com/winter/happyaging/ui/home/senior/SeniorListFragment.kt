package com.winter.happyaging.ui.home.senior

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.data.senior.service.SeniorService
import com.winter.happyaging.databinding.FragmentSeniorListBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import com.winter.happyaging.ui.home.RegisterSeniorFragment
import com.winter.happyaging.ui.home.adapter.SeniorAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeniorListFragment : Fragment() {

    private var _binding: FragmentSeniorListBinding? = null
    private val binding get() = _binding!!

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
        // RecyclerView 설정
        with(binding.recyclerSenior) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = seniorAdapter
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

    private fun fetchSeniorData() {
        val seniorService = RetrofitClient.getInstance(requireContext()).create(SeniorService::class.java)
        seniorService.getSeniorAllList().enqueue(object : Callback<ApiResponse<List<SeniorReadResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SeniorReadResponse>>>,
                response: Response<ApiResponse<List<SeniorReadResponse>>>,
            ) {
                if (response.isSuccessful) {
                    val seniorList = response.body()?.data.orEmpty()
                    Log.d("SeniorListFragment", "받아온 시니어 리스트: $seniorList")
                    seniorAdapter.updateData(seniorList)
                } else {
                    Log.e("SeniorListFragment", "API 호출 실패 - 상태 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<SeniorReadResponse>>>, t: Throwable) {
                Log.e("SeniorListFragment", "네트워크 오류: ${t.message}")
            }
        })
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