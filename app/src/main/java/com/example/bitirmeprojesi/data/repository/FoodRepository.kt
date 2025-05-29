package com.example.bitirmeprojesi.data.repository

import com.example.bitirmeprojesi.data.network.RetrofitClient

class FoodRepository {
    private val api = RetrofitClient.foodService

    suspend fun getAllFoods() = api.getAllFoods()
    suspend fun addToCart(yemekAdi: String, yemekResimAdi: String, yemekFiyat: Int, yemekSiparisAdet: Int, kullaniciAdi: String) =
        api.addToCart(yemekAdi, yemekResimAdi, yemekFiyat, yemekSiparisAdet, kullaniciAdi)
    suspend fun getCartItems(kullaniciAdi: String) = api.getCartItems(kullaniciAdi)
    suspend fun removeFromCart(sepetYemekId: Int, kullaniciAdi: String) = api.removeFromCart(sepetYemekId, kullaniciAdi)
}