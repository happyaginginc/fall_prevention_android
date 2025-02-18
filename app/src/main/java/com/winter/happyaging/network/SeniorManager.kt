package com.winter.happyaging.data.senior

import android.app.Application
import android.content.Context
import android.util.Log

object SeniorManager {
    private const val PREFS_NAME = "SeniorPrefs"
    private const val SENIOR_ID_KEY = "senior_id"
    private const val SENIOR_NAME_KEY = "senior_name"
    private const val SENIOR_ADDRESS_KEY = "senior_address"
    private const val SENIOR_BIRTH_YEAR_KEY = "senior_birth_year"

    // 시니어 정보 저장 (Application Context 사용) - Activity Context 아님
    fun saveSeniorData(
        context: Context,
        seniorId: Long,
        name: String,
        address: String,
        birthYear: Int
    ) {
        val prefs =
            context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putLong(SENIOR_ID_KEY, seniorId)
            putString(SENIOR_NAME_KEY, name)
            putString(SENIOR_ADDRESS_KEY, address)
            putInt(SENIOR_BIRTH_YEAR_KEY, birthYear)
            apply()
        }
        Log.d(
            "SeniorManager",
            "🚀 Senior 정보 저장 완료: ID=$seniorId, 이름=$name, 주소=$address, 출생년도=$birthYear"
        )

        val savedId = prefs.getLong(SENIOR_ID_KEY, -1L)
        val savedName = prefs.getString(SENIOR_NAME_KEY, "이름 없음")
        Log.d("SeniorManager", "🔍 저장 확인: ID=$savedId, 이름=$savedName")
    }

    // 시니어 ID 가져오기
    fun getSeniorId(context: Context): Long {
        val prefs =
            context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(SENIOR_ID_KEY, -1L).also {
            Log.d("SeniorManager", "📥 불러온 Senior ID: $it")
        }
    }

    // 시니어 이름 가져오기
    fun getSeniorName(context: Context): String {
        val prefs =
            context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        return prefs.getString(SENIOR_NAME_KEY, "이름 없음").also {
            Log.d("SeniorManager", "📥 불러온 Senior 이름: $it")
        } ?: "이름 없음"
    }

    // 시니어 주소 가져오기
    fun getSeniorAddress(context: Context): String {
        val prefs =
            context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SENIOR_ADDRESS_KEY, "주소 없음") ?: "주소 없음"
    }

    // 시니어 출생년도 가져오기
    fun getSeniorBirthYear(context: Context): Int {
        val prefs =
            context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(SENIOR_BIRTH_YEAR_KEY, -1)
    }
}
