package com.winter.happyaging.network.security.model

data class Token(
    val grantType: String,
    val tokenType: String,
    val value: String
)
