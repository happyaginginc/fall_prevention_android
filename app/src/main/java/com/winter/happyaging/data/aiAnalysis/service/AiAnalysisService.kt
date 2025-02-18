package com.winter.happyaging.data.aiAnalysis.service

import com.winter.happyaging.data.aiAnalysis.model.AiAnalysisResponse
import com.winter.happyaging.data.aiAnalysis.model.DateListResponse
import com.winter.happyaging.data.aiAnalysis.model.RoomRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AiAnalysisService {
    @POST("/roomAI/{seniorId}")
    fun analysisImages(
        @Path("seniorId") seniorId: Long,
        @Body request: List<RoomRequest>
    ): Call<AiAnalysisResponse>

    @GET("/roomAI/date/{seniorId}")
    fun getRecordDates(
        @Path("seniorId") seniorId: Long
    ): Call<DateListResponse>

    @GET("/roomAI/{seniorId}")
    fun getAnalysisByDate(
        @Path("seniorId") seniorId: Long,
        @Query("date") date: String
    ): Call<AiAnalysisResponse>
}