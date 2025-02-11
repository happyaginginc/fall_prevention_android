package com.winter.happyaging.survey

data class OptionData(
    val orderNumber: Int,
    val content: String, // Spinner - 예, 아니오
    val nextQuestionId: Int? // Null 처리
)
