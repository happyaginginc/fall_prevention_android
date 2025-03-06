package com.winter.happyaging.network

import android.content.Context

object SeniorManager {
    private const val PREFS_NAME = "SeniorPrefs"
    private const val SENIOR_ID_KEY = "senior_id"
    private const val SENIOR_NAME_KEY = "senior_name"
    private const val SENIOR_ADDRESS_KEY = "senior_address"
    private const val SENIOR_BIRTH_YEAR_KEY = "senior_birth_year"

    fun saveSeniorData(
        context: Context,
        seniorId: Long,
        name: String,
        address: String,
        birthYear: Int
    ) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putLong(SENIOR_ID_KEY, seniorId)
            putString(SENIOR_NAME_KEY, name)
            putString(SENIOR_ADDRESS_KEY, address)
            putInt(SENIOR_BIRTH_YEAR_KEY, birthYear)
            apply()
        }
    }

    fun getSeniorId(context: Context): Long =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(SENIOR_ID_KEY, -1L)

    fun getSeniorName(context: Context): String =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(SENIOR_NAME_KEY, "이름 없음") ?: "이름 없음"

    fun getSeniorAddress(context: Context): String =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(SENIOR_ADDRESS_KEY, "주소 없음") ?: "주소 없음"

    fun getSeniorBirthYear(context: Context): Int =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(SENIOR_BIRTH_YEAR_KEY, -1)
}
