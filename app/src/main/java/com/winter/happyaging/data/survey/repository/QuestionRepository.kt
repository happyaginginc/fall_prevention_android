package com.winter.happyaging.data.survey.repository

import android.content.Context
import android.util.Log
import com.winter.happyaging.data.survey.model.SurveyQuestionResponse
import com.winter.happyaging.data.survey.service.SurveyService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object QuestionRepository {

    private var cachedQuestionList: List<SurveyQuestionResponse>? = null

    fun getQuestionListOnce(context: Context, onComplete: () -> Unit) {
        if (cachedQuestionList != null) {
            onComplete()
            return
        }
        val service = RetrofitClient.getInstance(context).create(SurveyService::class.java)
        service.getAllQuestions().enqueue(object : Callback<ApiResponse<List<SurveyQuestionResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SurveyQuestionResponse>>>,
                response: Response<ApiResponse<List<SurveyQuestionResponse>>>
            ) {
                if (response.isSuccessful) {
                    cachedQuestionList = response.body()?.data.orEmpty()
                    Log.d("QuestionRepository", "질문 목록 불러오기 성공, size = ${cachedQuestionList?.size}")
                } else {
                    Log.e("QuestionRepository", "질문 목록 불러오기 실패: ${response.code()}")
                }
                onComplete()
            }

            override fun onFailure(call: Call<ApiResponse<List<SurveyQuestionResponse>>>, t: Throwable) {
                Log.e("QuestionRepository", "질문 목록 불러오기 오류: ${t.message}")
                onComplete()
            }
        })
    }

    fun getCachedQuestionList(): List<SurveyQuestionResponse> {
        return cachedQuestionList.orEmpty()
    }
}
