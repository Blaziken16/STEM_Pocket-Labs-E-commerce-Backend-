package com.example.pocketLabs.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object CartTable : IntIdTable("cart") {
    val user_id = reference("user_id", UsersTable)
    val product_id = reference("product_id", ProductTable)
    val quantity = integer("quantity")
}