package com.example.pocketLabs.routes

import com.example.pocketLabs.database.repositories.OrderRepository
import com.example.pocketLabs.models.PlaceOrderRequest
import com.example.pocketLabs.models.UpdateOrderStatusRequest
import com.example.pocketLabs.security.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.orderRoutes() {
    authenticate("auth-jwt") {
        route("/order") {
            post{
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    return@post
                }

                val request = call.receive<PlaceOrderRequest>()

                if (request.paymentMethod != "COD" && request.paymentMethod != "RAZORPAY") {
                    call.respond(HttpStatusCode.BadRequest, "Invalid payment method")
                    return@post
                }
                val order = OrderRepository.placeOrder(
                    userId = userId,
                    paymentMethod = request.paymentMethod
                )

                call.respond(HttpStatusCode.Created, order)
            }
            get("/my") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    return@get
                }

                val orders = OrderRepository.getOrderByUserId(userId)
                call.respond(HttpStatusCode.OK, orders)
            }
            patch("/{orderId}/cancel") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    return@patch
                }

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                if (orderId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid order id")
                    return@patch
                }

                val cancelled = OrderRepository.cancelOrder(userId, orderId)

                if (cancelled) {
                    call.respond(HttpStatusCode.OK, "Order cancelled successfully")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Order cannot be cancelled")
                }
            }
            patch("/{orderId}/payment") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    return@patch
                }

                val orderId = call.parameters["orderId"]?.toIntOrNull()
                if (orderId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid order id")
                    return@patch
                }

                val razorPayId = call.request.queryParameters["razorPayId"]
                if (razorPayId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing razorPayId")
                    return@patch
                }

                val updated = OrderRepository.updatePaymentStatus(orderId, razorPayId)

                if (updated) {
                    call.respond(HttpStatusCode.OK, "Payment updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Order not found")
                }
            }
        }
    }
    authenticate("auth-jwt") {
        route("/admin/orders"){
            get{
                val allOrders = OrderRepository.getAllOrders()
                call.respond(HttpStatusCode.OK, allOrders)
            }
            patch("/{orderId}/status") {
                val orderId = call.parameters["orderId"]?.toIntOrNull()
                if (orderId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid order id")
                    return@patch
                }

                val request = call.receive<UpdateOrderStatusRequest>()

                val allowedStatuses = setOf("PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED")
                if (request.status !in allowedStatuses) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid order status")
                    return@patch
                }

                val updated = OrderRepository.updateOrderStatus(orderId, request.status)

                if (updated) {
                    call.respond(HttpStatusCode.OK, "Order status updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Order not found")
                }
            }
        }
    }
}
