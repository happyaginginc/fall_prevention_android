package com.winter.happyaging.data.auth.service

import com.winter.happyaging.network.security.model.RefreshTokenResponse
import com.winter.happyaging.data.auth.model.response.UserInfoResponse
import com.winter.happyaging.data.auth.model.request.LoginRequest
import com.winter.happyaging.data.auth.model.request.RegisterRequest
import com.winter.happyaging.data.auth.model.response.LoginResponse
import com.winter.happyaging.data.auth.model.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @POST("/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/auth/refresh")
    fun refreshAccessToken(
        @Header("Authorization") authorization: String // "Bearer {refreshTokenValue}"
    ): Call<RefreshTokenResponse>

    @GET("/account/me")
    fun getUserInfo(
        @Header("Authorization") accessToken: String
    ): Call<UserInfoResponse>
}