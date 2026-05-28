package com.example.pocketLabs.plugins

import io.ktor.server.application.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.pocketLabs.config.EnvConfig
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond

fun Application.configureSecurity() {
    val jwtAudience = EnvConfig.jwtAudience
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = EnvConfig.jwtRealm
    val jwtSecret = EnvConfig.jwtSecret
    val jwtIssuer = EnvConfig.jwtIssuer
    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null){
                    JWTPrincipal(credential.payload)
                }else{
                    null
                }
            }
            challenge { _, _ ->
                call.respond(io.ktor.http.HttpStatusCode.Unauthorized,
                    "Token is Invalid or Expired"
                )
            }
        }
    }
}