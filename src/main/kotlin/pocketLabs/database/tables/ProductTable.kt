package com.example.pocketLabs.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ProductTable : IntIdTable("product") {
    val name = varchar("name", 255)
    val description = text("description")
    val price = decimal("price",10,2)
    val image_url = varchar("image_url",500)
    val category = varchar("category", 255)
    val stock = integer("stock")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}