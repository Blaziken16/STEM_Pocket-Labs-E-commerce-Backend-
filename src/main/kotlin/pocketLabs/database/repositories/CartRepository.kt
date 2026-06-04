package com.example.pocketLabs.database.repositories

import com.example.pocketLabs.database.tables.CartTable
import com.example.pocketLabs.database.tables.ProductTable
import com.example.pocketLabs.models.CartItemResponse
import com.example.pocketLabs.models.ProductResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.transactions.transaction


object CartRepository {
    fun getCartItemsByUserId(userId:Int): List<CartItemResponse> = transaction{
        CartTable.innerJoin(ProductTable)
            .selectAll()
            .where(CartTable.user_id.eq(userId))
            .map{ row->
                CartItemResponse(
                    itemId = row[CartTable.id].value,
                    userId = row[CartTable.user_id].value,
                    productId = row[CartTable.product_id].value,
                    quantity = row[CartTable.quantity],
                    product = ProductResponse(
                        id = row[ProductTable.id].value,
                        name = row[ProductTable.name],
                        description = row[ProductTable.description],
                        price = row[ProductTable.price].toDouble(),
                        stock = row[ProductTable.stock],
                        image_url = row[ProductTable.image_url],
                        category = row[ProductTable.category]
                    )
                )
            }
    }

    fun addToCart(userId:Int, productId: Int, quantity:Int) = transaction{
        val existing = CartTable
            .selectAll()
            .where { (CartTable.user_id eq userId) and (CartTable.product_id eq productId) }
            .singleOrNull()

        if (existing != null) {
            CartTable.update({ (CartTable.user_id eq userId) and (CartTable.product_id eq productId) }) {
                it[CartTable.quantity] = quantity
            }
        } else {
            CartTable.insert {
                it[user_id] = userId
                it[product_id] = productId
                it[CartTable.quantity] = quantity
            }
        }
    }

    fun removeFromCart(userId:Int, productId:Int): Boolean = transaction{
        val deletedRows = CartTable.deleteWhere {
            (CartTable.user_id eq userId) and (CartTable.product_id eq productId)
        }
        deletedRows > 0
    }
}