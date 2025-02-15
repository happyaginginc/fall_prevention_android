package com.winter.happyaging.home

import com.google.gson.annotations.SerializedName

data class SeniorData2(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("birthYear") val birthYear: Int,
    @SerializedName("sex") val sex: String,  // Enum 대신 String 사용
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("relationship") val relationship: String, // Enum 대신 String 사용
    @SerializedName("memo") val memo: String,
    @SerializedName("seniorId") val seniorId: Long
)
