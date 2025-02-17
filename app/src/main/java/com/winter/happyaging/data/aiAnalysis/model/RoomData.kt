package com.winter.happyaging.data.aiAnalysis.model

data class RoomData(
    var name: String,

    var guide1Images: MutableList<String> = mutableListOf(),
    var guide2Images: MutableList<String> = mutableListOf(),
    var guide3Images: MutableList<String> = mutableListOf()
)