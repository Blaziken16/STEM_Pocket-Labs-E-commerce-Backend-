package com.example.pocketLabs.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name : String,
    val email:String,
    val password: String
)
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val email: String,
)