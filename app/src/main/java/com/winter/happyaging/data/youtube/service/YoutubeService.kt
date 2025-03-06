package com.winter.happyaging.data.youtube.service

import com.winter.happyaging.data.youtube.model.YoutubePlaylistResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeService {
    @GET("/youtube/playlist/videos")
    fun getPlaylistVideos(@Query("playlistId") playlistId: String): Call<YoutubePlaylistResponse>
}