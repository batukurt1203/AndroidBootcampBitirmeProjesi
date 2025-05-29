package com.example.bitirmeprojesi.data.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://kasimadalan.pe.hu/"

    private val gson = GsonBuilder()
        .setLenient() // JSON yanıtlarını daha esnek bir şekilde işle
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val foodService: FoodService = retrofit.create(FoodService::class.java)
}