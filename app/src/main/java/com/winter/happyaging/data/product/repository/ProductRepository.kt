package com.winter.happyaging.data.product.repository

import android.content.Context
import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.data.product.service.ProductService
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository(private val context: Context) {

    companion object {
        // 앱 실행 중 한 번만 데이터를 받아오기 위한 캐시
        private var productListCache: List<ProductResponse>? = null
    }

    fun getProductList(onResult: (List<ProductResponse>) -> Unit) {
        if (productListCache != null) {
            onResult(productListCache!!)
        } else {
            fetchFromNetwork { products ->
                productListCache = products
                onResult(products)
            }
        }
    }

    fun refreshProductList(onResult: (List<ProductResponse>) -> Unit) {
        fetchFromNetwork { products ->
            productListCache = products
            onResult(products)
        }
    }

    private fun fetchFromNetwork(onResult: (List<ProductResponse>) -> Unit) {
        val productService = RetrofitClient.getInstance(context).create(ProductService::class.java)
        productService.getProductList().enqueue(object : Callback<ApiResponse<List<ProductResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<ProductResponse>>>,
                response: Response<ApiResponse<List<ProductResponse>>>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body()?.data.orEmpty())
                } else {
                    onResult(emptyList())
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<ProductResponse>>>, t: Throwable) {
                onResult(emptyList())
            }
        })
    }
}