package com.example.pocketLabs.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.h2.engine.User
import org.jetbrains.exposed.sql.kotlin.datetime.Date

class JwtServices (
    private val secret: String,
    private val issuer: String,
    private val audience: String
){
    fun generateToken(userId: Int,email:String): String{
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId",userId)
            .withClaim("email",email)
            .withExpiresAt(java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .sign(Algorithm.HMAC256(secret))
    }
}