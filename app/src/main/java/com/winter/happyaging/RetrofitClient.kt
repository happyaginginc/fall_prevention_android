package com.winter.happyaging

import android.content.Context
import com.winter.happyaging.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun getInstance(context: Context): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(context)) // ✅ 자동 토큰 추가 & 갱신
            .build()

        return Retrofit.Builder()
            .baseUrl("http://3.37.58.59/") // 서버 주소 확인
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
