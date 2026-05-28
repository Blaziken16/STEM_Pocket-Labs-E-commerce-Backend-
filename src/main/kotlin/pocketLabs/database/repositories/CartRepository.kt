package com.example.pocketLabs.database.repositories

import com.example.pocketLabs.database.tables.CartTable
import com.example.pocketLabs.models.CartItemResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


object CartRepository {
    fun getCartItemsByUserId(userId:Int): List<CartItemResponse> = transaction{
        CartTable
            .selectAll()
            .where(CartTable.user_id.eq(userId))
            .map{ row->
                CartItemResponse(
                    itemId = row[CartTable.id].value,
                    userId = row[CartTable.user_id].value,
                    productId = row[CartTable.product_id].value,
                    quantity = row[CartTable.quantity]
                )
            }
    }

    fun addToCart(userId:Int, productId: Int, quantity:Int) = transaction{
        CartTable
            .insert {
                it[user_id] = userId
                it[product_id] = productId
                it[CartTable.quantity] = quantity
            }
    }

    fun removeFromCart(userId:Int, productId:Int): Boolean = transaction{
        val deletedRows = CartTable.deleteWhere {
            (CartTable.user_id eq userId) and (CartTable.product_id eq productId)
        }
        deletedRows > 0
    }
}