package com.winter.happyaging.data.aiAnalysis.service

import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AiAnalysisService {
    @POST("/roomAI/{seniorId}")
    fun analysisImages(
        @Path("seniorId") seniorId: Long,
        @Body request: List<RoomRequest>
    ): Call<AiAnalysisResponse>
}
