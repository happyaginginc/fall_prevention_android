package com.winter.happyaging.service

import android.content.Context
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.winter.happyaging.R
import com.winter.happyaging.ResDTO.AIAnalysisResponse
import com.winter.happyaging.ResDTO.RoomRequest
import com.winter.happyaging.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AIAnalysisRepository {

    fun uploadRoomImages(
        context: Context,
        binding: View,
        seniorId: Int,
        roomRequests: List<RoomRequest>,
        onSuccess: (AIAnalysisResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val service = RetrofitClient.getInstance(context).create(AIAnalysisService::class.java)

        val gson = Gson()
        val json = gson.toJson(roomRequests) // JSON 변환

        service.analysisImages(seniorId, roomRequests)
            .enqueue(object : Callback<AIAnalysisResponse> {
                override fun onResponse(call: Call<AIAnalysisResponse>, response: Response<AIAnalysisResponse>) {
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
                }

                override fun onFailure(call: Call<AIAnalysisResponse>, t: Throwable) {
                    binding.findViewById<View>(R.id.loadingLayout)?.visibility = View.GONE
                    Log.e("AIAnalysisRepository", "네트워크 오류", t)
                    onFailure("네트워크 오류")
                }
            })
    }
}
