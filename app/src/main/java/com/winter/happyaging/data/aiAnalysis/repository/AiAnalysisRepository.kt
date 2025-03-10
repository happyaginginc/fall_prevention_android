package com.winter.happyaging.data.aiAnalysis.repository

import android.content.Context
import android.util.Log
import android.view.View
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.DateListResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomRequest
import com.winter.happyaging.data.aiAnalysis.service.AiAnalysisService
import com.winter.happyaging.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AiAnalysisRepository {
    fun uploadRoomImages(
        context: Context,
        binding: View,
        seniorId: Long,
        roomRequests: List<RoomRequest>,
        onSuccess: (AiAnalysisResponse) -> Unit,
        onFailure: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AiAnalysisService::class.java)
        service.analysisImages(seniorId, roomRequests)
            .enqueue(object : Callback<AiAnalysisResponse> {
                override fun onResponse(
                    call: Call<AiAnalysisResponse>,
                    response: Response<AiAnalysisResponse>
                ) {
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE
                    response.body()?.let { onSuccess(it) } ?: run {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        onFailure("Server error: $error")
                    }
                    onComplete()
                }
                override fun onFailure(call: Call<AiAnalysisResponse>, t: Throwable) {
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE
                    Log.e("AiAnalysisRepository", "Network error", t)
                    onFailure("Network error")
                    onComplete()
                }
            })
    }

    // 날짜별 기록 목록 가져오기
    fun getRecordDates(
        context: Context,
        seniorId: Long,
        onSuccess: (List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AiAnalysisService::class.java)
        service.getRecordDates(seniorId).enqueue(object : Callback<DateListResponse> {
            override fun onResponse(
                call: Call<DateListResponse>,
                response: Response<DateListResponse>
            ) {
                if (response.isSuccessful) {
                    val dates = response.body()?.data ?: emptyList()
                    onSuccess(dates)
                } else {
                    onFailure("Server error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<DateListResponse>, t: Throwable) {
                onFailure("Network error")
            }
        })
    }

    fun getAnalysisByDate(
        context: Context,
        seniorId: Long,
        date: String,
        onSuccess: (AiAnalysisResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AiAnalysisService::class.java)
        service.getAnalysisByDate(seniorId, date).enqueue(object : Callback<AiAnalysisResponse> {
            override fun onResponse(
                call: Call<AiAnalysisResponse>,
                response: Response<AiAnalysisResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onFailure("Empty response")
                } else {
                    onFailure("Server error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AiAnalysisResponse>, t: Throwable) {
                onFailure("Network error")
            }
        })
    }
}