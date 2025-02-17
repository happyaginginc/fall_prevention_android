package com.winter.happyaging.data.question.model

import com.winter.happyaging.data.product.model.ProductData

data class QuestionDTO(
    val content: String,
    val imageUrl: String?,
    val options: List<OptionData>,
    val questionCategory: String,
    val questionType: String,
    val products: List<ProductData>
)
