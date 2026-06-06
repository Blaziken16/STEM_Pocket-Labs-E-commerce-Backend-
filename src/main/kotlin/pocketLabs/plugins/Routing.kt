package com.example.pocketLabs.plugins

import com.example.pocketLabs.models.UserResponse
import com.example.pocketLabs.database.repositories.UserRepository
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
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        allowMethod(io.ktor.http.HttpMethod.Patch)

        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)

        allowCredentials = true
    }
    routing {
        authRoutes()

        authenticate("auth-jwt") {
            get("/me"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                
                val user = UserRepository.getUserById(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    UserResponse(
                        id = user.id.toString(),
                        email = user.email,
                        name = user.name,
                        isPremium = false,
                        memberSince = "Recently",
                        role = user.role
                    )
                )
            }
        }
        productRoutes()
        cartRoutes()
        orderRoutes()

    }
}
