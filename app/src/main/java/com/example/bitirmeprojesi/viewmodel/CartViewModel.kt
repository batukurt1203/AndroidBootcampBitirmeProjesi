package com.example.bitirmeprojesi.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.bitirmeprojesi.data.model.CartItem
import com.example.bitirmeprojesi.data.network.RetrofitClient
import com.example.bitirmeprojesi.data.repository.FoodRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CartViewModel : ViewModel() {
    private val repo = FoodRepository()
    val cartItems = MutableLiveData<List<CartItem>>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()
 //   private val api = RetrofitClient.foodService

    fun fetchCart(kullaniciAdi: String) {
        viewModelScope.launch {
            loading.value = true
            try {
                Log.d("CartViewModel", "Fetching cart for user: $kullaniciAdi")
                val response = repo.getCartItems(kullaniciAdi)
                Log.d("CartViewModel", "Response received: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    Log.d("CartViewModel", "Cart response: $cartResponse")
                    
                    if (cartResponse?.success == 1) {
                        val items = cartResponse.sepet_yemekler ?: emptyList()
                        cartItems.value = items
                        Log.d("CartViewModel", "Cart items updated: ${items.size} items")
                        
                        if (items.isEmpty()) {
                            error.value = "Sepetiniz boş"
                        }
                    } else {
                        error.value = "Sepet alınamadı"
                        Log.e("CartViewModel", "Failed to get cart")
                    }
                } else {
                    error.value = "Sunucu hatası: ${response.code()}"
                    Log.e("CartViewModel", "Server error: ${response.code()}")
                }
            } catch (e: HttpException) {
                error.value = "Sunucu hatası: ${e.code()}"
                Log.e("CartViewModel", "HTTP error: ${e.code()}", e)
            } catch (e: IOException) {
        //        error.value = "İnternet bağlantısı hatası"
                Log.e("CartViewModel", "Network error", e)
            } catch (e: Exception) {
                error.value = "Beklenmeyen bir hata oluştu"
                Log.e("CartViewModel", "Unexpected error", e)
            } finally {
                loading.value = false
            }
        }
    }

    fun removeFromCart(sepetYemekId: Int, kullaniciAdi: String) {
        viewModelScope.launch {
            try {
                val response = repo.removeFromCart(sepetYemekId, kullaniciAdi)
                if (response.isSuccessful) {
                    val basicResponse = response.body()
                    if (basicResponse?.success == 1) {
                        // Mevcut sepet öğelerini güncelle
                        val currentItems = cartItems.value?.toMutableList() ?: mutableListOf()
                        currentItems.removeAll { it.sepet_yemek_id.toInt() == sepetYemekId }
                        cartItems.value = currentItems
                        
                        // Eğer sepet boşsa, boş liste olarak güncelle
                        if (currentItems.isEmpty()) {
                            cartItems.value = emptyList()
                            error.value = "Sepetiniz boş"
                        }
                    } else {
                        error.value = "Ürün sepetten silinemedi"
                    }
                } else {
                    error.value = "Sunucu hatası: ${response.code()}"
                }
            } catch (e: HttpException) {
                error.value = "Sunucu hatası: ${e.code()}"
            } catch (e: IOException) {
                error.value = "İnternet bağlantısı hatası"
            } catch (e: Exception) {
                error.value = "Beklenmeyen bir hata oluştu"
            }
        }
    }

    fun confirmOrder(kullaniciAdi: String) {
        viewModelScope.launch {
            loading.value = true
            try {
                // Sepetteki tüm ürünleri sil
                cartItems.value?.forEach { item ->
                    repo.removeFromCart(item.sepet_yemek_id.toInt(), kullaniciAdi)
                }
                // Sepeti temizle
                if (cartItems.value?.size == 0) {
                    error.value = "Sepetiniz boş!"
                } else {
                    cartItems.value = emptyList()
                    error.value = "Siparişiniz alındı!"
                }
            } catch (e: HttpException) {
                error.value = "Sunucu hatası: ${e.code()}"
            } catch (e: IOException) {
                // error.value = "İnternet bağlantısı hatası"
                Log.e("CartViewModel", "Network error", e)
            } catch (e: Exception) {
                error.value = "Beklenmeyen bir hata oluştu"
            } finally {
                loading.value = false
            }
        }
    }

    fun addToCart(
        yemekAdi: String,
        yemekResimAdi: String,
        yemekFiyat: Int,
        yemekSiparisAdet: Int,
        kullaniciAdi: String
    ) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = repo.addToCart(yemekAdi, yemekResimAdi, yemekFiyat, yemekSiparisAdet, kullaniciAdi)
                if (response.isSuccessful) {
                    val basicResponse = response.body()
                    if (basicResponse?.success == 1) {
                        // Sepeti güncelle
                        fetchCart(kullaniciAdi)
                    } else {
                        error.value = "Ürün sepete eklenemedi"
                    }
                } else {
                    error.value = "Sunucu hatası: ${response.code()}"
                }
            } catch (e: HttpException) {
                error.value = "Sunucu hatası: ${e.code()}"
            } catch (e: IOException) {
                error.value = "İnternet bağlantısı hatası"
            } catch (e: Exception) {
                error.value = "Beklenmeyen bir hata oluştu"
            } finally {
                loading.value = false
            }
        }
    }

    fun clearCart(kullaniciAdi: String) {
        viewModelScope.launch {
            loading.value = true
            try {
                // Sepetteki tüm ürünleri sil
                cartItems.value?.forEach { item ->
                    repo.removeFromCart(item.sepet_yemek_id.toInt(), kullaniciAdi)
                }
                // Sepeti temizle
                cartItems.value = emptyList()
                error.value = "Sepet temizlendi"
            } catch (e: HttpException) {
                error.value = "Sunucu hatası: ${e.code()}"
            } catch (e: IOException) {
        //        error.value = "İnternet bağlantısı hatası"
            } catch (e: Exception) {
                error.value = "Beklenmeyen bir hata oluştu"
            } finally {
                loading.value = false
            }
        }
    }
}