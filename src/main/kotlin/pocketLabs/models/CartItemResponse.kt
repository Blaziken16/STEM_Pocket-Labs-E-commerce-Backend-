package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse (
    val itemId : Int,
    val userId : Int,
    val productId : Int,
    val quantity : Int,
    val product: ProductResponse? = null
)