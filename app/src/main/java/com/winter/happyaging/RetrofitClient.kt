package com.winter.happyaging

import android.content.Context
import com.winter.happyaging.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        private var retrofit: Retrofit? = null

        fun getInstance(context: Context): Retrofit {
            if (retrofit == null) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)

                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(context))
                    .addInterceptor(logging)  // 로그 추가
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl("http://3.37.58.59/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit!!
        }
    }
}
