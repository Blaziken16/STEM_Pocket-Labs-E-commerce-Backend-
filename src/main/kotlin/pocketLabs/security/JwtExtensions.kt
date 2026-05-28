package com.example.pocketLabs.security

import io.ktor.server.auth.jwt.JWTPrincipal


fun JWTPrincipal.userId(): Int ? = payload.getClaim("userId").asInt()
fun JWTPrincipal.email(): String ? = payload.getClaim("email").asString()