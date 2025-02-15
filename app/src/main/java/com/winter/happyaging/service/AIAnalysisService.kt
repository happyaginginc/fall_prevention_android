package com.winter.happyaging.service

import com.winter.happyaging.ResDTO.AIAnalysisResponse
import com.winter.happyaging.ResDTO.RoomRequest
import retrofit2.Call
import retrofit2.http.*

interface AIAnalysisService {
    @Headers("Content-Type: application/json")
    @POST("/roomAI/{seniorId}")
    fun analysisImages(
        @Path("seniorId") seniorId: Int,
        @Body request: List<RoomRequest>
    ): Call<AIAnalysisResponse>
}
