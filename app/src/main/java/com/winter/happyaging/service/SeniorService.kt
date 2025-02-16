package com.winter.happyaging.service

import com.winter.happyaging.ResDTO.ApiResponse
import com.winter.happyaging.ResDTO.SeniorResponse
import com.winter.happyaging.home.SeniorData
import com.winter.happyaging.home.SeniorData2
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SeniorService {
    // 시니어 추가 API
    @POST("/senior")
    fun registerSenior(@Body request: SeniorData): Call<SeniorResponse>

    // 자신의 시니어 조회 API
    @GET("/senior/me/2")
    fun getSeniorList(): Call<ApiResponse<SeniorData2>>

    // 자신의 시니어 전체 조회 API
    @GET("senior/me")
    fun getSeniorAllList(): Call<ApiResponse<List<SeniorData>>>
}