package com.winter.happyaging.ReqDTO

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String
)
