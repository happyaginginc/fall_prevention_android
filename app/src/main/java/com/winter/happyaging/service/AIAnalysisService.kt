package com.winter.happyaging.service

import com.winter.happyaging.ResDTO.AIAnalysisResponse
import com.winter.happyaging.ResDTO.AIAnalysisRequest
import com.winter.happyaging.ResDTO.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface AIAnalysisService {
    @POST("roomAI/{seniorId}")
    fun analysisImages(
        @Path("seniorId") seniorId: Int,
        @Header("Authorization") token: String,
        @Body request: AIAnalysisRequest // JSON 형태로 데이터 전송
    ): Call<AIAnalysisResponse>
}
