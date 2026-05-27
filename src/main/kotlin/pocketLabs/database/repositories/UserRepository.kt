package com.example.pocketLabs.database.repositories

import com.example.pocketLabs.database.DatabaseFactory
import com.example.pocketLabs.database.tables.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object UserRepository {

    suspend fun findUserByEmail(email: String): Triple<Int, String, String>? {
        return DatabaseFactory.dbQuery {
            UsersTable.selectAll()
                .where(UsersTable.email.eq(email))
                .map{
                    Triple(
                        it[UsersTable.id].value,
                        it[UsersTable.name],
                        it[UsersTable.passwordHash]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun createUser(email: String, name: String, passwordHash: String):Int {
        return DatabaseFactory.dbQuery {
            UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.name] = name
                it[UsersTable.passwordHash] = passwordHash
            }[UsersTable.id].value
        }
    }
}