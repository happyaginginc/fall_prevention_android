package com.winter.happyaging.network.security.model

import com.winter.happyaging.ResDTO.Token

data class TokenData(
    val accessToken: Token,
    val refreshToken: Token
)
