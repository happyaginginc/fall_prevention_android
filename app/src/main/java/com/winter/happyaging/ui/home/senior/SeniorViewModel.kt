package com.winter.happyaging.ui.home.senior

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.data.senior.repository.SeniorRepository

class SeniorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SeniorRepository(application.applicationContext)
    private val _seniorList = MutableLiveData<List<SeniorReadResponse>>()
    val seniorList: LiveData<List<SeniorReadResponse>> get() = _seniorList

    // 앱 실행 시 혹은 처음 화면에 진입할 때 호출
    fun fetchSeniorList() {
        repository.getSeniorList { seniors ->
            _seniorList.postValue(seniors)
        }
    }

    // 스와이프 새로고침이나 Senior 추가 후 강제 새로고침
    fun refreshSeniorList() {
        repository.refreshSeniorList { seniors ->
            _seniorList.postValue(seniors)
        }
    }
}