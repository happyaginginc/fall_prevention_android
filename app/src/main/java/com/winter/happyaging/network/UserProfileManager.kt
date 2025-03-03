package com.winter.happyaging.network

import android.content.Context
import android.util.Log

object UserProfileManager {
    private const val PREF_NAME = "UserProfilePrefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"

    fun saveUserInfo(context: Context, userId: Int, email: String, name: String, phone: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_PHONE, phone)
            apply()
        }
        Log.d("UserProfileManager", "사용자 정보 저장 완료: $userId, $email, $name, $phone")
    }

    fun clearUserInfo(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Log.d("UserProfileManager", "사용자 정보 삭제 완료")
    }

    fun getUserId(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, -1)
    }

    fun getUserEmail(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserPhone(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USER_PHONE, "") ?: ""
    }
}