package com.example.bitirmeprojesi.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    val yemek_id: String,
    val yemek_adi: String,
    val yemek_resim_adi: String,
    val yemek_fiyat: String
) : Parcelable