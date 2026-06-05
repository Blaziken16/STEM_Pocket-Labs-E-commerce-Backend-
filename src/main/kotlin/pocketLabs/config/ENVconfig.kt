package com.example.pocketLabs.config


import io.github.cdimascio.dotenv.dotenv

object EnvConfig {
    private val env = dotenv {
        ignoreIfMissing = true
    }

    val dbUrl: String = env["DB_URL"]
        ?: error("DB_URL is missing in .env")

    val dbUser: String = env["DB_USER"]
        ?: error("DB_USER is missing in .env")

    val dbPassword: String = env["DB_PASSWORD"]
        ?: error("DB_PASSWORD is missing in .env")

    val jwtSecret: String = env["JWT_SECRET"]
        ?: error("JWT_SECRET is missing in .env")

    val jwtIssuer: String = env["JWT_ISSUER"] ?: "pocketlabs-backend"
    val jwtAudience: String = env["JWT_AUDIENCE"] ?: "pocketlabs-users"
    val jwtRealm: String = env["JWT_REALM"] ?: "Access to PocketLabs API"
    val port: String = env["PORT"] ?: "8080"
}