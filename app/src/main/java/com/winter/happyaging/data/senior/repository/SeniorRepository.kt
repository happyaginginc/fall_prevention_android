package com.winter.happyaging.data.senior.repository

import android.content.Context
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.data.senior.service.SeniorService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeniorRepository(private val context: Context) {

    companion object {
        // 앱 실행 중 한 번만 데이터를 받아오기 위한 캐시
        private var seniorListCache: List<SeniorReadResponse>? = null
    }

    // 캐시가 존재하면 캐시 반환, 없으면 네트워크 호출 후 캐시 저장
    fun getSeniorList(onResult: (List<SeniorReadResponse>) -> Unit) {
        if (seniorListCache != null) {
            onResult(seniorListCache!!)
        } else {
            fetchFromNetwork { seniors ->
                seniorListCache = seniors
                onResult(seniors)
            }
        }
    }

    // 강제 새로고침 시 네트워크 호출 후 캐시 업데이트
    fun refreshSeniorList(onResult: (List<SeniorReadResponse>) -> Unit) {
        fetchFromNetwork { seniors ->
            seniorListCache = seniors
            onResult(seniors)
        }
    }

    private fun fetchFromNetwork(onResult: (List<SeniorReadResponse>) -> Unit) {
        val seniorService = RetrofitClient.getInstance(context).create(SeniorService::class.java)
        seniorService.getSeniorAllList().enqueue(object : Callback<ApiResponse<List<SeniorReadResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SeniorReadResponse>>>,
                response: Response<ApiResponse<List<SeniorReadResponse>>>
            ) {
                if (response.isSuccessful) {
                    val seniors = response.body()?.data.orEmpty()
                    onResult(seniors)
                } else {
                    onResult(emptyList())
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<SeniorReadResponse>>>, t: Throwable) {
                onResult(emptyList())
            }
        })
    }
}