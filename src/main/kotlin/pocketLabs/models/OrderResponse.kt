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
    val items: List<OrderItemResponse>,
    val razorPayId: String? = null
)

@Serializable
data class OrderItemResponse(
    val productId: String,
    val name: String,
    val quantity: Int,
    val pricePaid: Double,
    val image: String
)