package com.winter.happyaging

import android.content.Context
import com.winter.happyaging.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private var retrofit: Retrofit? = null

        fun getInstance(context: Context): Retrofit {
            if (retrofit == null) {
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(context))
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
