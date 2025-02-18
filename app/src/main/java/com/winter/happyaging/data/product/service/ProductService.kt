package com.winter.happyaging.data.product.service

import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.network.model.ApiResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductService {
    @GET("/product")
    fun getProductList(): Call<ApiResponse<List<ProductResponse>>>
}