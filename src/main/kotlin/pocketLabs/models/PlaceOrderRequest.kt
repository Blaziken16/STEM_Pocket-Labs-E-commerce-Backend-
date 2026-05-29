package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaceOrderRequest(
    val cancelReason: String? = null,
    val paymentMethod: String? = null
)