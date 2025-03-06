package com.winter.happyaging.data.youtube.model

data class YoutubePlaylistResponse(
    val status: Int,
    val data: List<YoutubeVideo>
)