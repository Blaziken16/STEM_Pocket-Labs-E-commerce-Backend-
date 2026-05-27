package com.example.pocketLabs.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UsersTable : IntIdTable("users") {
    val name = varchar("name", 255)

    val email = varchar("email", 255).uniqueIndex()

    val passwordHash = varchar("password_hash", 255)

    val role = varchar("role", 255).default("customer")

    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}