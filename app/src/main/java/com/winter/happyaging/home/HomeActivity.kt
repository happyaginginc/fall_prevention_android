package com.winter.happyaging.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.ReqDTO.SeniorRequest
import com.winter.happyaging.ResDTO.ApiResponse
import com.winter.happyaging.ResDTO.SeniorResponse
import com.winter.happyaging.RetrofitClient
import com.winter.happyaging.service.SeniorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var seniorAdapter: SeniorAdapter
    private lateinit var btnRegisterSenior: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerSenior)
        recyclerView.layoutManager = LinearLayoutManager(this)

        seniorAdapter = SeniorAdapter(emptyList()) { selectedSenior ->
            val intent = Intent(this, ManageSeniorActivity::class.java).apply {
                putExtra("name", selectedSenior.name)
                putExtra("address", selectedSenior.address)
                putExtra("birthYear", selectedSenior.birthYear)
                putExtra("sex", selectedSenior.sex)
                putExtra("phoneNumber", selectedSenior.phoneNumber)
                putExtra("relationship", selectedSenior.relationship)
                putExtra("memo", selectedSenior.memo)
            }
            startActivity(intent)
        }
        recyclerView.adapter = seniorAdapter

        fetchSeniorData()

        btnRegisterSenior = findViewById(R.id.btnRegisterSenior)

        btnRegisterSenior.setOnClickListener {
            val fragment = RegisterSeniorFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer2, fragment) // 프래그먼트 교체
                .addToBackStack(null) // 뒤로 가기 가능하게 설정
                .commit()
        }
    }

    fun fetchSeniorData() {
        val seniorService = RetrofitClient.getInstance(this).create(SeniorService::class.java)
        seniorService.getSeniorAllList().enqueue(object : Callback<ApiResponse<List<SeniorData>>> {
            override fun onResponse(call: Call<ApiResponse<List<SeniorData>>>, response: Response<ApiResponse<List<SeniorData>>>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val seniorList = apiResponse?.data ?: emptyList()

                    Log.d("HomeActivity", "받아온 시니어 리스트: $seniorList")
                    seniorAdapter.updateData(seniorList)
                } else {
                    Log.e("HomeActivity", "API 호출 실패 - 상태 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<SeniorData>>>, t: Throwable) {
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
