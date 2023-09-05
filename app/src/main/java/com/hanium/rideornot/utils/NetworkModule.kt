package com.hanium.rideornot.utils

import com.hanium.rideornot.BuildConfig.BASE_URL
import com.hanium.rideornot.data.ArrivalService
import com.hanium.rideornot.data.AuthService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object NetworkModule {
    fun getArrivalService(
    ): ArrivalService {
        return retrofit.create(ArrivalService::class.java)
    }

    fun getAuthService() : AuthService {
        return retrofit.create(AuthService::class.java)
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient(AppInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient
            = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    class AppInterceptor: Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            val token = ""
            val authRequest = request().newBuilder()
                    //TODO: SharedPreferences에 jwt 저장, 불러오기
                .addHeader("Authorization", "Bearer $token").build()
            proceed(authRequest)
        }
    }
}

