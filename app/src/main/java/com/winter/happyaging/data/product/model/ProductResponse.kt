package com.winter.happyaging.data.product.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("productId") val productId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String
)