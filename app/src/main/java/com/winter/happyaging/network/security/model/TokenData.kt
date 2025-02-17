package com.winter.happyaging.network.security.model

data class TokenData(
    val accessToken: Token,
    val refreshToken: Token
)
