package com.winter.happyaging.data.aiAnalysis.model

data class RoomData(
    var name: String,
    var guides: MutableList<GuideData> = mutableListOf()
)