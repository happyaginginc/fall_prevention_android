package com.winter.happyaging.auth.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String
)
