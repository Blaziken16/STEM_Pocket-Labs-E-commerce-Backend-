package com.example.pocketLabs.routes

import com.example.pocketLabs.database.repositories.CartRepository
import com.example.pocketLabs.models.AddToCartRequest
import com.example.pocketLabs.models.CartItemResponse
import com.example.pocketLabs.security.userId
import io.ktor.client.request.request
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.cartRoutes() {
    authenticate("auth-jwt") {
        route("/cart") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
                    return@get
                }

                val cartItems = CartRepository.getCartItemsByUserId(userId = userId)
                call.respond(HttpStatusCode.OK, cartItems)
            }

            post("/add") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
                    return@post
                }
                val request = call.receive<AddToCartRequest>()

                CartRepository.addToCart(
                    userId = userId,
                    productId = request.productId,
                    quantity = request.quantity,
                )
                val updatedCart = CartRepository.getCartItemsByUserId(userId)
                call.respond(HttpStatusCode.OK, updatedCart)
            }

            delete("/{productId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()
                val productId = call.parameters["productId"]?.toIntOrNull()


                if (productId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Product Id")
                    return@delete
                }
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
                    return@delete
                }


                val remove = CartRepository.removeFromCart(
                    userId = userId,
                    productId = productId
                )
                if(remove){
                    val updatedCart = CartRepository.getCartItemsByUserId(userId)
                    call.respond(HttpStatusCode.OK, updatedCart)
                }else{
                    call.respond(HttpStatusCode.NotFound, "Item not found")
                }
            }
        }
    }
}