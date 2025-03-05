package com.winter.happyaging.data.survey.model

data class UserAnswer(
    var answerText: String? = null,
    var optionNumbers: List<Int> = emptyList()
)
