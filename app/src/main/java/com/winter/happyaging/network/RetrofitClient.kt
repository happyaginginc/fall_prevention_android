package com.winter.happyaging.network

import android.content.Context
import com.winter.happyaging.network.security.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient 싱글턴
 */
class RetrofitClient private constructor() {

    companion object {
        private const val BASE_URL = "http://3.37.58.59/"
        @Volatile
        private var retrofit: Retrofit? = null

        fun getInstance(context: Context): Retrofit {
            return retrofit ?: synchronized(this) {
                retrofit ?: buildRetrofit(context).also { retrofit = it }
            }
        }

        private fun buildRetrofit(context: Context): Retrofit {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }
}