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

    fun fetchSeniorList() {
        try {
            repository.getSeniorList { seniors ->
                _seniorList.postValue(seniors)
            }
        } catch (e: Exception) {
            _seniorList.postValue(emptyList())
        }
    }

    fun refreshSeniorList() {
        try {
            repository.refreshSeniorList { seniors ->
                if (seniors == null) {
                    _seniorList.postValue(emptyList())
                } else {
                    _seniorList.postValue(seniors)
                }
            }
        } catch (e: Exception) {
            _seniorList.postValue(emptyList())
        }
    }
}