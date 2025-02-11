package com.winter.happyaging.ReqDTO

import com.winter.happyaging.home.SeniorData

data class SeniorRequest(
    val status: Int,
    val data: SeniorData
)
