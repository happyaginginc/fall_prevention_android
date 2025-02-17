package com.winter.happyaging.data.question.model

data class Question(
    val id: Int,
    val text: String,
    val choices: List<String>
)