package com.winter.happyaging.data.aiAnalysis.repository

import android.content.Context
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
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
        seniorId: Int,
        roomRequests: List<RoomRequest>,
        onSuccess: (AiAnalysisResponse) -> Unit,
        onFailure: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AiAnalysisService::class.java)

        val gson = Gson()
        val json = gson.toJson(roomRequests) // JSON 변환

        service.analysisImages(seniorId, roomRequests)
            .enqueue(object : Callback<AiAnalysisResponse> {
                override fun onResponse(call: Call<AiAnalysisResponse>, response: Response<AiAnalysisResponse>) {
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE
                    Log.d("AIAnalysisRepository", "서버로 보낼 최종 JSON 데이터: $json")
                    Log.d("AIAnalysisRepository", "서버로 보낼 데이터: $roomRequests")
                    if (response.isSuccessful) {
                        onSuccess(response.body()!!)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("AIAnalysisRepository", "서버 응답 오류: $errorBody")
                        onFailure("서버 응답 오류: $errorBody")
                    }
                    onComplete()
                }

                override fun onFailure(call: Call<AiAnalysisResponse>, t: Throwable) {
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE
                    Log.e("AIAnalysisRepository", "네트워크 오류", t)
                    onFailure("네트워크 오류")
                    onComplete()
                }
            })
    }
}