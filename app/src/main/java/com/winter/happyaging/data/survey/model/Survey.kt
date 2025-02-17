package com.winter.happyaging.data.survey.model

import com.winter.happyaging.data.question.model.Question

data class Survey(
    val totalQuestions: Int,
    val questions: List<Question>
)