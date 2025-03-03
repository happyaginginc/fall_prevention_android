package com.winter.happyaging.data.survey.service;

import com.winter.happyaging.data.survey.model.SurveyCreateRequest
import com.winter.happyaging.data.survey.model.SurveyCreateResponse
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SurveyService {

    /**
     * 질문 목록 조회
     * GET /question
     */
    @GET("/question")
    fun getAllQuestions(): Call<ApiResponse<List<SurveyQuestionResponse>>>


    /**
     * 설문 제출
     * POST /survey/{seniorId}
     */
    @POST("/survey/{seniorId}")
    fun submitSurvey(
            @Path("seniorId") seniorId: Long,
            @Body request: SurveyCreateRequest
    ): Call<SurveyCreateResponse>
}