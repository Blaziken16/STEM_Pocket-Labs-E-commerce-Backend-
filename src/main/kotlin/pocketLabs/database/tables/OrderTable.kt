package com.example.pocketLabs.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.time
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OrderTable : IntIdTable("orders") {
    val user_id = reference("user_id", UsersTable)
    val totalAmount = decimal("total_amount",10,2)
    val paymentStatus = varchar("payment_status",100).default("PENDING")
    val orderStatus = varchar("order_status",100).default("PROCESSING")
    val razorPayId = varchar("razor_pay_id",2048).nullable()
    val created_at = timestamp("created_at").defaultExpression(CurrentTimestamp)
}