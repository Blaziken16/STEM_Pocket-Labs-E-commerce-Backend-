package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val image_url: String,
    val category: String
)