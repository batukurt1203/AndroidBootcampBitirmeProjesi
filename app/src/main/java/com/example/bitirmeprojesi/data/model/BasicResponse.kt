package com.example.bitirmeprojesi.data.model
import java.io.Serializable


data class BasicResponse(
    val success: Int,
    val message: String?
): Serializable