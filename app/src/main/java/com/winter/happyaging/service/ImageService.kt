package com.winter.happyaging.service

import com.winter.happyaging.ResDTO.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {
    @Multipart
    @POST("storage/images")
    fun uploadImages(
        @Part image: MultipartBody.Part
    ): Call<ImageResponse>
}