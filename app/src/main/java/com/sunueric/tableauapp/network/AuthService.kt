package com.sunueric.tableauapp.network

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface AuthService {
    @POST("/api/authenticate")
    @FormUrlEncoded
    fun signIn(@Field("email") email: String, @Field("password") password: String): Call<AuthResponse>
}

data class AuthResponse(val accessToken: String)

object RetrofitClient {

    private const val BASE_URL = "https://your.api.base.url/" // Change this to your actual base URL

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideAuthService(): AuthService {
    return RetrofitClient.retrofit.create(AuthService::class.java)
}