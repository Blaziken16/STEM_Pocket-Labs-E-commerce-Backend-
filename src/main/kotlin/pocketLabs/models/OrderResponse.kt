package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse (
    val orderId: Int,
    val userId: Int,
    val totalAmount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: String,
    val razorPayId: String? = null
)