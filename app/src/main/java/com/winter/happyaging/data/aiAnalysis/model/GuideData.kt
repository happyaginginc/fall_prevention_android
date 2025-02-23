package com.winter.happyaging.data.aiAnalysis.model

data class GuideData(
    var guideText: String,
    var images: MutableList<String> = mutableListOf()
)