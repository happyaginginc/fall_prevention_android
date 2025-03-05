package com.winter.happyaging.data.survey.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SurveyResultData(
    @SerializedName("surveyId") val surveyId: Int,
    @SerializedName("seniorId") val seniorId: Int,
    @SerializedName("pdfUrl") val pdfUrl: String?,      // null 일 수도 있으니 ?
    @SerializedName("riskLevel") val riskLevel: Int,
    @SerializedName("summary") val summary: String?
) : Serializable
