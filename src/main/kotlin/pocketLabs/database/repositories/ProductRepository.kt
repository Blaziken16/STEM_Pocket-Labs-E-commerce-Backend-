package com.example.pocketLabs.database.repositories

import com.example.pocketLabs.database.tables.CartTable.id
import com.example.pocketLabs.database.tables.ProductTable
import com.example.pocketLabs.database.tables.ProductTable.category
import com.example.pocketLabs.database.tables.ProductTable.description
import com.example.pocketLabs.database.tables.ProductTable.image_url
import com.example.pocketLabs.database.tables.ProductTable.name
import com.example.pocketLabs.database.tables.ProductTable.price
import com.example.pocketLabs.database.tables.ProductTable.stock
import com.example.pocketLabs.models.CreateProductRequest
import com.example.pocketLabs.models.ProductResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object ProductRepository{
    fun createProduct(request: CreateProductRequest) = transaction {
        val insertedRow = ProductTable.insert{
            it[name] = request.name
            it[description] = request.description
            it[price] = request.price.toBigDecimal()
            it[stock] = request.stock
            it[image_url] = request.image_url
            it[category] = request.category
        }.resultedValues?.first()
            ?: error("Failed to insert product")
        ProductResponse(
            id = insertedRow[ProductTable.id].value,
            name = insertedRow[name],
            description = insertedRow[description],
            price = insertedRow[price].toDouble(),
            stock = insertedRow[stock],
            image_url = insertedRow[image_url],
            category = insertedRow[category]
        )
    }
    fun getAllProducts():List<ProductResponse> = transaction {
        ProductTable
            .selectAll()
            .map {
                row ->
                ProductResponse(
                    id = row[ProductTable.id].value,
                    name = row[ProductTable.name],
                    description = row[ProductTable.description],
                    price = row[ProductTable.price].toDouble(),
                    stock = row[ProductTable.stock],
                    image_url = row[ProductTable.image_url],
                    category = row[ProductTable.category]
                )
            }
    }
    fun getProductById(productId:Int):ProductResponse? = transaction{
        ProductTable
            .selectAll()
            .where(ProductTable.id.eq(productId))
            .map{
                row->
                ProductResponse(
                    id = row[ProductTable.id].value,
                    name = row[ProductTable.name],
                    description = row[ProductTable.description],
                    price = row[ProductTable.price].toDouble(),
                    stock = row[ProductTable.stock],
                    image_url = row[ProductTable.image_url],
                    category = row[ProductTable.category]
                )
            }
        .singleOrNull()
    }
    fun deleteProduct(productId: Int): Boolean = transaction {
        ProductTable.deleteWhere { ProductTable.id.eq(productId) } > 0
    }
    fun updateProduct(productId: Int, request: CreateProductRequest): ProductResponse? = transaction {
        val updatedRows = ProductTable.update({ ProductTable.id eq productId }) {
            it[name] = request.name
            it[description] = request.description
            it[price] = request.price.toBigDecimal()
            it[stock] = request.stock
            it[image_url] = request.image_url
            it[category] = request.category
        }

        if (updatedRows == 0) {
            null
        } else {
            ProductTable
                .selectAll()
                .where { ProductTable.id eq productId }
                .map { row ->
                    ProductResponse(
                        id = row[ProductTable.id].value,
                        name = row[ProductTable.name],
                        description = row[ProductTable.description],
                        price = row[ProductTable.price].toDouble(),
                        stock = row[ProductTable.stock],
                        image_url = row[ProductTable.image_url],
                        category = row[ProductTable.category]
                    )
                }
                .singleOrNull()
        }
    }
}
