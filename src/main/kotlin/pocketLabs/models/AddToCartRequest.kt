package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    val productId: Int,
    val quantity: Int,
)