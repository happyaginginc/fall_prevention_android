package com.winter.happyaging.ResDTO

import com.winter.happyaging.network.security.model.TokenData

data class RefreshTokenResponse(
    val status: Int,
    val data: TokenData
)
