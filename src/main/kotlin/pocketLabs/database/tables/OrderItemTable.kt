package com.example.pocketLabs.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object OrderItemTable: IntIdTable("order_item") {
    val order_id = reference("order_id", OrderTable)
    val product_id = reference("product_id", ProductTable)
    val quantity = integer("quantity")
    val priceAtPurchase = decimal("price_at_purchase",10,2)
}