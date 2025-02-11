package com.winter.happyaging.survey

data class QuestionDTO(
    val content: String,
    val imageUrl: String?,
    val options: List<OptionData>,
    val questionCategory: String,
    val questionType: String,
    val products: List<ProductData>
)
