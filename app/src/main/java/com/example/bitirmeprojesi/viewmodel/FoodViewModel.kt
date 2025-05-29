package com.example.bitirmeprojesi.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.bitirmeprojesi.data.model.Food
import com.example.bitirmeprojesi.data.network.RetrofitClient
import kotlinx.coroutines.launch

class FoodViewModel : ViewModel() {
    val foods = MutableLiveData<List<Food>?>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()
    val api = RetrofitClient.foodService

    fun fetchFoods() {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val response = api.getAllFoods()
                if (response.isSuccessful) {
                    val foodList = response.body()?.yemekler
                    if (foodList != null) {
                        foods.value = foodList
                    } else {
                        error.value = "Yemek listesi boş"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("FoodViewModel", "API error body: $errorBody")
                    error.value = "Sunucu hatası: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("FoodViewModel", "API Hatası", e)
                error.value = "Bağlantı hatası: ${e.localizedMessage}"
            } finally {
                loading.value = false
            }
        }
    }
}