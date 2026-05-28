package com.example.pocketLabs.database.repositories


import com.example.pocketLabs.database.tables.OrderTable
import com.example.pocketLabs.models.OrderResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object OrderRepository{
    fun getOrderByUserId(userId:Int): List<OrderResponse> = transaction {
        OrderTable
            .select(OrderTable.user_id eq userId)
            .map{ row->
                OrderResponse(
                    orderId = row[OrderTable.id].value,
                    userId = row[OrderTable.user_id].value,
                    totalAmount = row[OrderTable.totalAmount].toDouble(),
                    status = row[OrderTable.orderStatus]
                )
            }


    }

}