package com.winter.happyaging.ui.home.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.data.product.repository.ProductRepository

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository(application.applicationContext)
    private val _productList = MutableLiveData<List<ProductResponse>>()
    val productList: LiveData<List<ProductResponse>> get() = _productList

    fun fetchProductList() {
        try {
            repository.getProductList { products ->
                _productList.postValue(products)
            }
        } catch (e: Exception) {
            _productList.postValue(emptyList())
        }
    }

    fun refreshProductList() {
        try {
            repository.refreshProductList { products ->
                _productList.postValue(products)
            }
        } catch (e: Exception) {
            _productList.postValue(emptyList())
        }
    }
}