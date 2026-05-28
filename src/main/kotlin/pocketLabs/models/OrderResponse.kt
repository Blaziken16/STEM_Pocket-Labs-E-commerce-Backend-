package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse (
    val orderId: Int,
    val userId: Int,
    val totalAmount: Double,
    val status: String
)