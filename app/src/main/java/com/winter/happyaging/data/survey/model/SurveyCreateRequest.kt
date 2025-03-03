package com.winter.happyaging.data.survey.model

data class SurveyCreateRequest(
    val responses: List<SurveyAnswerRequest>
)

data class SurveyAnswerRequest(
    val questionNumber: Int,
    val answerText: String?,      // SHORT_ANSWER 인 경우 사용
    val optionNumber: List<Int>   // MULTIPLE_CHOICE, TRUE_FALSE 경우 해당 optionNumber 리스트
)