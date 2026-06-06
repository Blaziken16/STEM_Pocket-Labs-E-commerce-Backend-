package com.example.pocketLabs.config


import io.github.cdimascio.dotenv.dotenv

object EnvConfig {
    private val env = dotenv {
        ignoreIfMissing = true
    }

    val dbUrl: String = System.getenv("DB_URL") ?: env["DB_URL"]
        ?: error("DB_URL is missing in environment variables")

    val dbUser: String = System.getenv("DB_USER") ?: env["DB_USER"]
        ?: error("DB_USER is missing in environment variables")

    val dbPassword: String = System.getenv("DB_PASSWORD") ?: env["DB_PASSWORD"]
        ?: error("DB_PASSWORD is missing in environment variables")

    val jwtSecret: String = System.getenv("JWT_SECRET") ?: env["JWT_SECRET"]
        ?: error("JWT_SECRET is missing in environment variables")

    val jwtIssuer: String = System.getenv("JWT_ISSUER") ?: env["JWT_ISSUER"] ?: "pocketlabs-backend"
    val jwtAudience: String = System.getenv("JWT_AUDIENCE") ?: env["JWT_AUDIENCE"] ?: "pocketlabs-users"
    val jwtRealm: String = System.getenv("JWT_REALM") ?: env["JWT_REALM"] ?: "Access to PocketLabs API"
    val port: String = System.getenv("PORT") ?: env["PORT"] ?: "8080"
}