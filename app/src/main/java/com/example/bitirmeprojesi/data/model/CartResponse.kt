package com.example.bitirmeprojesi.data.model

import java.io.Serializable


data class CartResponse(
    val sepet_yemekler: List<CartItem>?,
    val success: Int
): Serializable