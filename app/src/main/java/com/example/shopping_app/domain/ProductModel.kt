package com.example.shopping_app.domain

data class ProductModel(
    val id: Long = getId(),
    val name: String,
    var purchased: Boolean = false
) {
    companion object {
        var count = 0L
        fun getId(): Long {
            count++
            return count
        }
    }
}