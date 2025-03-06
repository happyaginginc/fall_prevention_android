package com.winter.happyaging.data.youtube.repository

import com.winter.happyaging.data.youtube.model.YoutubePlaylistResponse
import com.winter.happyaging.data.youtube.model.YoutubeVideo
import com.winter.happyaging.data.youtube.service.YoutubeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KnowledgeRepository(private val service: YoutubeService) {

    fun fetchVideos(callback: (List<YoutubeVideo>) -> Unit) {
        val playlistIds = listOf(
            "PLcgCdNTzpN1iHjLXuON6k_lkg29cHDCMT",
            "PLcgCdNTzpN1jTfxuh_-Q2BwDkh7BUHDf5"
        )
        val allVideos = mutableListOf<YoutubeVideo>()
        var responsesReceived = 0

        playlistIds.forEach { playlistId ->
            service.getPlaylistVideos(playlistId).enqueue(object : Callback<YoutubePlaylistResponse> {
                override fun onResponse(call: Call<YoutubePlaylistResponse>, response: Response<YoutubePlaylistResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { playlistResponse ->
                            if (playlistResponse.status == 200) {
                                allVideos.addAll(playlistResponse.data)
                            }
                        }
                    }
                    responsesReceived++
                    if (responsesReceived == playlistIds.size) {
                        callback(allVideos)
                    }
                }
                override fun onFailure(call: Call<YoutubePlaylistResponse>, t: Throwable) {
                    responsesReceived++
                    if (responsesReceived == playlistIds.size) {
                        callback(allVideos)
                    }
                }
            })
        }
    }
}