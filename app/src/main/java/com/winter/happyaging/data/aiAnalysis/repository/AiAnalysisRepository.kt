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
        seniorId: Long,
        roomRequests: List<RoomRequest>,
        onSuccess: (AiAnalysisResponse) -> Unit,
        onFailure: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AiAnalysisService::class.java)
        val json = Gson().toJson(roomRequests)

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
}