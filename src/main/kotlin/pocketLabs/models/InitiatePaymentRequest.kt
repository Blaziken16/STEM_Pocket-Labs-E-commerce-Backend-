package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class InitiatePaymentRequest(
    val paymentMethod: String
)