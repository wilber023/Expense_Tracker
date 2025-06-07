package com.example.expensetracker.src.core.network

import android.util.Log
import com.example.expensetracker.src.home.data.dataSource.local.remote.ExpenseApi
import com.example.expensetracker.src.login.data.dataSource.local.remote.LoginApi
import com.example.expensetracker.src.register.data.dataSource.remote.RegisterApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "http://54.86.190.9/"
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("NetworkModule", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val loginApi: LoginApi by lazy {
        retrofit.create(LoginApi::class.java)
    }

    val expenseApi: ExpenseApi by lazy {
        retrofit.create(ExpenseApi::class.java)
    }

    val registerApi: RegisterApi by lazy {
        retrofit.create(RegisterApi::class.java)
    }
}