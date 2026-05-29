package com.example.pocketLabs.plugins

import com.example.pocketLabs.models.MeResponse
import com.example.pocketLabs.routes.authRoutes
import com.example.pocketLabs.routes.cartRoutes
import com.example.pocketLabs.routes.orderRoutes
import com.example.pocketLabs.routes.productRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(CORS){
        anyHost()
    }
    routing {
        authRoutes()

        authenticate("auth-jwt") {
            get("/me"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val email = principal.payload.getClaim("email").asString()
                call.respond(
                    HttpStatusCode.OK,
                    MeResponse(
                        userId = userId,
                        email = email,
                    )
                )
            }
        }
        productRoutes()
        cartRoutes()
        orderRoutes()

    }
}
