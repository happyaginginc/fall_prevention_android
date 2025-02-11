package com.winter.happyaging.ResDTO

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: T
)
