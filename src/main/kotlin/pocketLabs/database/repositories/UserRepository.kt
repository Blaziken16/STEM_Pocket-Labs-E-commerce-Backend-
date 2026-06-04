package com.example.pocketLabs.database.repositories

import com.example.pocketLabs.database.DatabaseFactory
import com.example.pocketLabs.database.tables.UsersTable
import com.example.pocketLabs.models.UserInfo
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

object UserRepository {

    suspend fun findUserByEmail(email: String): UserInfo? {
        return DatabaseFactory.dbQuery {
            UsersTable.selectAll()
                .where(UsersTable.email.eq(email))
                .map{
                    UserInfo(
                        id = it[UsersTable.id].value,
                        name = it[UsersTable.name],
                        email = it[UsersTable.email],
                        passwordHashed = it[UsersTable.passwordHash],
                        role = it[UsersTable.role]
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

    suspend fun getUserById(id: Int): UserInfo? {
        return DatabaseFactory.dbQuery {
            UsersTable.selectAll()
                .where(UsersTable.id.eq(id))
                .map{
                    UserInfo(
                        id = it[UsersTable.id].value,
                        name = it[UsersTable.name],
                        email = it[UsersTable.email],
                        passwordHashed = it[UsersTable.passwordHash],
                        role = it[UsersTable.role]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun updateUserRole(id: Int, role: String): Boolean {
        return DatabaseFactory.dbQuery {
            UsersTable.update({ UsersTable.id eq id }) {
                it[UsersTable.role] = role
            } > 0
        }
    }
}