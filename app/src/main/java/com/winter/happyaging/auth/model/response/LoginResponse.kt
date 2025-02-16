package com.winter.happyaging.auth.model.response

import com.winter.happyaging.network.security.model.TokenData

data class LoginResponse(
    val status: Int,
    val data: TokenData
)
