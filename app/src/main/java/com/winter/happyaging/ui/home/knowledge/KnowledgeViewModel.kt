package com.winter.happyaging.ui.home.knowledge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.winter.happyaging.data.youtube.model.YoutubeVideo
import com.winter.happyaging.data.youtube.repository.KnowledgeRepository
import com.winter.happyaging.data.youtube.service.YoutubeService
import com.winter.happyaging.network.RetrofitClient

class KnowledgeViewModel(application: Application): AndroidViewModel(application) {
    private val youtubeService = RetrofitClient.getInstance(application.applicationContext)
        .create(YoutubeService::class.java)
    private val repository = KnowledgeRepository(youtubeService)

    private val _videoList = MutableLiveData<List<YoutubeVideo>>()
    val videoList: LiveData<List<YoutubeVideo>> get() = _videoList

    private var hasFetchedData = false

    fun fetchVideos(isRefresh: Boolean = false) {
        if (!isRefresh && hasFetchedData) return

        repository.fetchVideos { videos ->
            _videoList.postValue(videos)
            hasFetchedData = true
        }
    }
}