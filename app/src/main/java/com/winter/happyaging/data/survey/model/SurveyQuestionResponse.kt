package com.winter.happyaging.data.survey.model

import com.google.gson.annotations.SerializedName
import com.winter.happyaging.data.product.model.ProductData

data class SurveyQuestionResponse(
    @SerializedName("questionNumber") val questionNumber: Int,
    @SerializedName("content") val content: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("category") val category: String,
    @SerializedName("type") val type: String,  // MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    @SerializedName("options") val options: List<SurveyOption>,
    @SerializedName("products") val products: List<ProductData>
)

data class SurveyOption(
    @SerializedName("optionNumber") val optionNumber: Int,
    @SerializedName("content") val content: String,
    @SerializedName("nextQuestionNumber") val nextQuestionNumber: Int? = null
)