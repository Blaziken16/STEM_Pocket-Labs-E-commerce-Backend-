package com.example.pocketLabs.routes

import com.example.pocketLabs.database.repositories.ProductRepository
import com.example.pocketLabs.database.repositories.UserRepository
import com.example.pocketLabs.models.CreateProductRequest
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
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.productRoutes() {
    route("/products") {
        get{
            val products = ProductRepository.getAllProducts()
            call.respond(HttpStatusCode.OK,products)
        }
        get("/{id}") {
            val productId = call.parameters["id"]?.toIntOrNull()
            if(productId == null) {
                call.respond(HttpStatusCode.NotFound,"Product not found")
                return@get
            }
            val product = ProductRepository.getProductById(productId)
            if(product == null) {
                call.respond(HttpStatusCode.NotFound,"Product not found")
                return@get
            }else{
                call.respond(HttpStatusCode.OK,product)
            }
        }

        authenticate("auth-jwt") {
            post{
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()

                if(userId == null){
                    call.respond(HttpStatusCode.Unauthorized,"Unauthorized")
                    return@post
                }
                val user = UserRepository.getUserById(userId)
                if(user == null){
                    call.respond(HttpStatusCode.NotFound,"User not found")
                    return@post
                }
                if(user.role != "admin"){
                    call.respond(HttpStatusCode.Forbidden,"Only Admins can add and delete products")
                    return@post
                }
                val request = call.receive<CreateProductRequest>()
                val createdProduct = ProductRepository.createProduct(request)

                call.respond(HttpStatusCode.Created,createdProduct)
            }
            delete("/{id}"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if(userId == null){
                    call.respond(HttpStatusCode.Unauthorized,"Invalid Token")
                    return@delete
                }
                val user = UserRepository.getUserById(userId)
                if(user == null){
                    call.respond(HttpStatusCode.NotFound,"User not found")
                    return@delete
                }
                if (user.role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Only admin can delete products")
                    return@delete
                }

                val productId = call.parameters["id"]?.toIntOrNull()
                if (productId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid product id")
                    return@delete
                }

                val deleted = ProductRepository.deleteProduct(productId)
                if (!deleted) {
                    call.respond(HttpStatusCode.NotFound, "Product not found")
                } else {
                    call.respond(HttpStatusCode.OK, "Product deleted successfully")
                }
            }
            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
                    return@put
                }

                val user = UserRepository.getUserById(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@put
                }

                if (user.role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Only admin can update products")
                    return@put
                }

                val productId = call.parameters["id"]?.toIntOrNull()
                if (productId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid product id")
                    return@put
                }

                val request = call.receive<CreateProductRequest>()

                val updatedProduct = ProductRepository.updateProduct(productId, request)

                if (updatedProduct == null) {
                    call.respond(HttpStatusCode.NotFound, "Product not found")
                } else {
                    call.respond(HttpStatusCode.OK, updatedProduct)
                }
            }
        }

    }
}