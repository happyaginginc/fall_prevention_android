package com.winter.happyaging.network.security.model

data class RefreshTokenResponse(
    val status: Int,
    val data: TokenData
)
