package com.example.pocketLabs.routes

import com.example.pocketLabs.database.repositories.UserRepository
import com.example.pocketLabs.models.AuthResponse
import com.example.pocketLabs.models.UserResponse
import com.example.pocketLabs.models.LoginRequest
import com.example.pocketLabs.models.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.mindrot.jbcrypt.BCrypt
import com.example.pocketLabs.config.EnvConfig
import com.example.pocketLabs.security.JwtServices


fun Route.authRoutes() {
    val jwtSecret = EnvConfig.jwtSecret
    val jwtIssuer = EnvConfig.jwtIssuer
    val jwtAudience = EnvConfig.jwtAudience

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
                user = UserResponse(
                    id = userId.toString(),
                    email = request.email,
                    name = request.name,
                    isPremium = false,
                    memberSince = "Recently",
                    role = "user"
                )
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

            val userId = existingUser.id
            val email = existingUser.email
            val StoredPasswordHash = existingUser.passwordHashed
            val name = existingUser.name

            val isPasswordCorrect = BCrypt.checkpw(request.password, StoredPasswordHash)

            if(!isPasswordCorrect){
                call.respond(HttpStatusCode.Unauthorized,"Incorrect Password")
                return@post
            }
            val token = jwtService.generateToken(userId,email)
            val response = AuthResponse(
                token = token,
                user = UserResponse(
                    id = userId.toString(),
                    email = email,
                    name = name,
                    isPremium = false,
                    memberSince = "Recently",
                    role = existingUser.role
                )
            )
            call.respond(HttpStatusCode.OK, response)
        }
        get("/make-me-admin") {
            val email = call.request.queryParameters["email"]
            if (email == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing email parameter")
                return@get
            }
            val user = UserRepository.findUserByEmail(email)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }
            UserRepository.updateUserRole(user.id, "admin")
            call.respond(HttpStatusCode.OK, "Success! User $email is now an admin. Please log out and log back in on the frontend.")
        }
    }
}