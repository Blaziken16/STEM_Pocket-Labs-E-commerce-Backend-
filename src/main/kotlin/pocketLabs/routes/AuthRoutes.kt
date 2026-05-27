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
import com.example.pocketLabs.security.JwtServices


fun Route.authRoutes() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()

    val jwtService = JwtServices(
        secret = jwtSecret,
        issuer = jwtIssuer,
        audience = jwtAudience
    )

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
            val token = jwtService.generateToken(userId,request.email)
            val response = AuthResponse(
                token = token,
                userId = userId ,
                email = request.email
            )
            call.respond(HttpStatusCode.Created, response)
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
            val token = jwtService.generateToken(userId,email)
            val response = AuthResponse(
                token = token,
                userId = userId ,
                email = email
            )
            call.respond(HttpStatusCode.OK, response)
        }
    }
}