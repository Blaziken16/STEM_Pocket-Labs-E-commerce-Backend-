package com.example.pocketLabs.routes

import com.example.pocketLabs.database.repositories.UserRepository
import com.example.pocketLabs.models.AuthResponse
import com.example.pocketLabs.models.LoginRequest
import com.example.pocketLabs.models.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.mindrot.jbcrypt.BCrypt

fun Route.authRoutes() {
    route("/auth") {
        post("/register"){
            val request = call.receive<RegisterRequest>()

            val existingUser = UserRepository.findUserByEmail(request.email)
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict,"Email already Registered")
                return@post
            }

            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

            val userId = UserRepository.createUser(
                email = request.email,
                name = request.name,
                passwordHash = hashedPassword
            )
            val response = AuthResponse(
                token = "dummy",
                userId = userId ,
                email = request.email
            )
        }
        post("/login"){
            val request = call.receive<LoginRequest>()

            val existingUser = UserRepository.findUserByEmail(request.email)
            if (existingUser == null) {
                call.respond(HttpStatusCode.Unauthorized,"Invalid Email and Password")
                return@post
            }

            val userId = existingUser.first
            val email = existingUser.second
            val StoredPasswordHash = existingUser.third

            val isPasswordCorrect = BCrypt.checkpw(request.password, StoredPasswordHash)

            if(!isPasswordCorrect){
                call.respond(HttpStatusCode.Unauthorized,"Incorrect Password")
                return@post
            }

            val response = AuthResponse(
                token = "dummy",
                userId = userId ,
                email = email
            )
            call.respond(HttpStatusCode.OK, response)
        }
    }
}