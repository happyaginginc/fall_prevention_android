package com.winter.happyaging.data.survey.service

import com.winter.happyaging.data.survey.model.SurveyCreateRequest
import com.winter.happyaging.data.survey.model.SurveyCreateResponse
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.data.survey.model.SurveyResultData
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

    /**
     * 설문 결과 조회
     * GET /survey/{seniorId}/{surveyId}
     */
    @GET("/survey/{seniorId}/{surveyId}")
    fun getSurveyResult(
        @Path("seniorId") seniorId: Long,
        @Path("surveyId") surveyId: Int
    ): Call<ApiResponse<SurveyResultData>>

    /**
     * 전체 설문 목록 조회 (시니어 기준)
     * GET /survey/senior/{seniorId}
     */
    @GET("/survey/senior/{seniorId}")
    fun getAllSurveyResults(
        @Path("seniorId") seniorId: Long
    ): Call<ApiResponse<List<SurveyResultData>>>
}
