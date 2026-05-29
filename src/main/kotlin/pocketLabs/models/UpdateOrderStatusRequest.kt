package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderStatusRequest(
    val status: String
)