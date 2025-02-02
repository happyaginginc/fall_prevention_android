package com.winter.happyaging.ReqDTO

data class SeniorRequest(
    val name: String,
    val address: String,
    val birthYear: Int,
    val sex: Sex,
    val phoneNumber: String,
    val relationship: Relationship,
    val memo: String
)

enum class Sex {
    MALE, FEMALE
}

enum class Relationship {
    SELF, FAMILY, ETC
}
