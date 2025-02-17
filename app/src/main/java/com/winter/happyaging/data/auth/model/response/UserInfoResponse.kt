package com.winter.happyaging.data.auth.model.response

data class UserInfoResponse(
    val id: Int,
    val email: String,
    val name: String,
    val phoneNumber: String
)
