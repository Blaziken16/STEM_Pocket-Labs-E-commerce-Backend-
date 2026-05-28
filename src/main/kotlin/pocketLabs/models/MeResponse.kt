package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val userId: Int,
    val email: String,
)