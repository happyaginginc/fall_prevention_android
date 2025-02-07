package com.winter.happyaging.home

data class Question(
    val id: Int,
    val text: String,
    val choices: List<String>
)