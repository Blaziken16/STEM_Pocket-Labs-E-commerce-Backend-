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
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val isPremium: Boolean,
    val memberSince: String,
    val role: String
)

@Serializable
data class UserInfo(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHashed: String,
    val role: String,
)