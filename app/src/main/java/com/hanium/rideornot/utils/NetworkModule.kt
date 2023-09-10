package com.hanium.rideornot.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hanium.rideornot.App
import com.hanium.rideornot.BuildConfig.BASE_URL
import com.hanium.rideornot.data.ArrivalService
import com.hanium.rideornot.data.AuthService
import com.hanium.rideornot.data.ProfileService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

object NetworkModule {
    fun getArrivalService(
    ): ArrivalService {
        return retrofit.create(ArrivalService::class.java)
    }

    fun getAuthService(): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    fun getProfileService(): ProfileService {
        return authRetrofit.create(ProfileService::class.java)
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient(AppInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient = OkHttpClient.Builder().run {
        addInterceptor(interceptor)
        build()
    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val jwt = App.preferenceUtil.getJwt()
            val authRequest = request().newBuilder()
                .addHeader("Authorization", "$jwt").build()
            proceed(authRequest)
        }
    }
}

