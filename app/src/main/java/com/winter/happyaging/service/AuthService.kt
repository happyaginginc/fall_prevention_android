package com.winter.happyaging.service

import com.winter.happyaging.ReqDTO.LoginRequest
import com.winter.happyaging.ReqDTO.RefreshTokenRequest
import com.winter.happyaging.ReqDTO.RegisterRequest
import com.winter.happyaging.ResDTO.LoginResponse
import com.winter.happyaging.ResDTO.RefreshTokenResponse
import com.winter.happyaging.ResDTO.RegisterResponse
import com.winter.happyaging.ResDTO.UserInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    // 회원가입 API
    @Headers("Content-Type: application/json")
    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    // 로그인 API
    @POST("/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 토큰 재발급 API
    @POST("/auth/refresh")
    fun refreshAccessToken(@Body request: RefreshTokenRequest): Call<RefreshTokenResponse>

//    // 로그아웃 API
//    @POST("/auth/logout")
//    fun logout(@Header("Authorization") accessToken: String): Call<Void>

    // 자신의 개인정보 조회 API (로그인 여부 확인)
    @GET("/account/me")
    fun getUserInfo(accessToken: String): Call<UserInfoResponse>
}
