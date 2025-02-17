package com.winter.happyaging.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.data.senior.service.SeniorService
import com.winter.happyaging.databinding.ActivityHomeBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import com.winter.happyaging.ui.home.adapter.SeniorAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val seniorAdapter = SeniorAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding.recyclerSenior) {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = seniorAdapter
        }

        fetchSeniorData()

        binding.btnRegisterSenior.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer2, RegisterSeniorFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    fun fetchSeniorData() {
        val seniorService = RetrofitClient.getInstance(this).create(SeniorService::class.java)
        seniorService.getSeniorAllList().enqueue(object : Callback<ApiResponse<List<SeniorReadResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SeniorReadResponse>>>,
                response: Response<ApiResponse<List<SeniorReadResponse>>>
            ) {
                if (response.isSuccessful) {
                    val seniorList = response.body()?.data.orEmpty()
                    Log.d("HomeActivity", "받아온 시니어 리스트: $seniorList")
                    seniorAdapter.updateData(seniorList)
                } else {
                    Log.e("HomeActivity", "API 호출 실패 - 상태 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<SeniorReadResponse>>>, t: Throwable) {
                Log.e("HomeActivity", "네트워크 오류: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeActivity", "onResume 호출됨 - fetchSeniorData() 실행")
        fetchSeniorData() // 화면이 다시 보일 때마다 데이터 갱신
    }
}