package com.winter.happyaging.data.senior.service

import com.winter.happyaging.data.senior.model.SeniorCreateRequest
import com.winter.happyaging.data.senior.model.SeniorCreateResponse
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SeniorService {
    // 시니어 추가 API
    @POST("/senior")
    fun registerSenior(@Body request: SeniorCreateRequest): Call<SeniorCreateResponse>

    // 자신의 시니어 전체 조회 API
    @GET("senior/me")
    fun getSeniorAllList(): Call<ApiResponse<List<SeniorReadResponse>>>
}