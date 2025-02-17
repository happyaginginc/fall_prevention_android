package com.winter.happyaging.data.auth.model.response

import com.winter.happyaging.network.security.model.TokenData

data class LoginResponse(
    val status: Int,
    val data: TokenData
)
