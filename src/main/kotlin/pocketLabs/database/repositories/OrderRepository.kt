package com.example.pocketLabs.database.repositories


import com.example.pocketLabs.database.tables.CartTable
import com.example.pocketLabs.database.tables.OrderItemTable
import com.example.pocketLabs.database.tables.OrderTable
import com.example.pocketLabs.database.tables.OrderTable.orderStatus
import com.example.pocketLabs.database.tables.OrderTable.paymentStatus
import com.example.pocketLabs.database.tables.ProductTable
import com.example.pocketLabs.models.OrderItemResponse
import com.example.pocketLabs.models.OrderResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object OrderRepository{
    private fun getOrderItems(orderId: Int): List<OrderItemResponse> {
        return OrderItemTable.innerJoin(ProductTable)
            .selectAll()
            .where { OrderItemTable.order_id eq orderId }
            .toList()
            .groupBy { it[OrderItemTable.product_id] }
            .map { (productId, rows) ->
                val firstRow = rows.first()
                OrderItemResponse(
                    productId = productId.toString(),
                    name = firstRow[ProductTable.name],
                    quantity = rows.sumOf { it[OrderItemTable.quantity] },
                    pricePaid = firstRow[OrderItemTable.priceAtPurchase].toDouble(),
                    image = firstRow[ProductTable.image_url]
                )
            }
    }

    fun getOrderByUserId(userId:Int): List<OrderResponse> = transaction {
        OrderTable
            .selectAll()
            .where{OrderTable.user_id eq userId}
            .map{ row->
                OrderResponse(
                    orderId = row[OrderTable.id].value,
                    userId = row[OrderTable.user_id].value,
                    totalAmount = row[OrderTable.totalAmount].toDouble(),
                    paymentMethod = row[OrderTable.paymentMethod],
                    paymentStatus = row[OrderTable.paymentStatus],
                    orderStatus = row[OrderTable.orderStatus],
                    items = getOrderItems(row[OrderTable.id].value),
                    razorPayId = row[OrderTable.razorPayId]
                )
            }


    }
    fun placeOrder(userId: Int, paymentMethod: String): OrderResponse {
        return transaction {

            val cartItems = CartTable
                .selectAll()
                .where{ CartTable.user_id eq userId }
                .toList()

            if (cartItems.isEmpty()) {
                error("Cart is empty")
            }

            val totalAmount = cartItems.sumOf { cartRow ->
                val price = ProductTable
                    .selectAll()
                    .where { ProductTable.id eq cartRow[CartTable.product_id] }
                    .first()[ProductTable.price]
                price.toDouble() * cartRow[CartTable.quantity]
            }

            val orderId = OrderTable.insertAndGetId {
                it[OrderTable.user_id]       = userId
                it[OrderTable.totalAmount]   = totalAmount.toBigDecimal()
                it[OrderTable.paymentMethod] = paymentMethod
                it[OrderTable.paymentStatus] = "PENDING"
                it[OrderTable.orderStatus]   = "PROCESSING"
            }.value


            cartItems.forEach { cartRow ->
                val productPrice = ProductTable
                    .selectAll()
                    .where{ ProductTable.id eq cartRow[CartTable.product_id] }
                    .first()[ProductTable.price]

                OrderItemTable.insert {
                    it[OrderItemTable.order_id]          = orderId
                    it[OrderItemTable.product_id]        = cartRow[CartTable.product_id]
                    it[OrderItemTable.quantity]          = cartRow[CartTable.quantity]
                    it[OrderItemTable.priceAtPurchase]   = productPrice
                }
            }

            CartTable.deleteWhere { CartTable.user_id eq userId }

            OrderResponse(
                orderId      = orderId,
                userId       = userId,
                totalAmount  = totalAmount.toDouble(),
                orderStatus   = "PROCESSING",
                paymentMethod = paymentMethod,
                paymentStatus = "PENDING",
                items = getOrderItems(orderId),
                razorPayId = null
            )
        }
    }
    fun cancelOrder(userId: Int, orderId: Int): Boolean {
        return transaction {
            val order = OrderTable
                .selectAll()
                .where{ (OrderTable.id eq orderId) and (OrderTable.user_id eq userId) }
                .firstOrNull() ?: return@transaction false


            if (order[OrderTable.orderStatus] != "PROCESSING") {
                return@transaction false
            }

            OrderTable.update({ (OrderTable.id eq orderId) and (OrderTable.user_id eq userId) }) {
                it[orderStatus]   = "CANCELLED"
                it[paymentStatus] = "CANCELLED"
            }
            true
        }
    }
    fun getAllOrders(): List<OrderResponse> {
        return transaction {
            OrderTable
                .selectAll()
                .map { row ->
                    OrderResponse(
                        orderId     = row[OrderTable.id].value,
                        userId      = row[OrderTable.user_id].value,
                        totalAmount = row[OrderTable.totalAmount].toDouble(),
                        paymentMethod = row[OrderTable.paymentMethod],
                        paymentStatus = row[OrderTable.paymentStatus],
                        orderStatus = row[OrderTable.orderStatus],
                        items = getOrderItems(row[OrderTable.id].value),
                        razorPayId = row[OrderTable.razorPayId]
                    )
                }
        }
    }
    fun updateOrderStatus(orderId: Int, newStatus: String): Boolean {
        return transaction {
            val updated = OrderTable.update({ OrderTable.id eq orderId }) {
                it[orderStatus] = newStatus

                if (newStatus == "DELIVERED") {
                    it[paymentStatus] = "SUCCESSFUL"
                }
            }
            updated > 0
        }
    }
    fun updatePaymentStatus(orderId: Int, razorPayId: String): Boolean {
        return transaction {
            val updated = OrderTable.update({ OrderTable.id eq orderId }) {
                it[paymentStatus] = "PAID"
                it[OrderTable.razorPayId] = razorPayId
            }
            updated > 0
        }
    }


}