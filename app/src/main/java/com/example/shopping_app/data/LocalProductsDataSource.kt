package com.example.shopping_app.data

import com.example.shopping_app.domain.ProductModel

class LocalProductsDataSource {

    private val products = mutableListOf<ProductModel>(
        ProductModel(name = "Ананас"),
        ProductModel(name = "Апельсин"),
        ProductModel(name = "Анаконда"),
        ProductModel(name = "Вода"),
        ProductModel(name = "Водка"),
        ProductModel(name = "Салфетки"),
        ProductModel(name = "Салат"),
        ProductModel(name = "Ягоды"),
        ProductModel(name = "Ежевика"),
        ProductModel(name = "Сосиски"),
        ProductModel(name = "Мандарины"),
        ProductModel(name = "Апельсины"),
        ProductModel(name = "Шампанское"),
        ProductModel(name = "Вино"),
    )

    fun getProducts(): List<ProductModel> = products.toList()

    fun addProduct(product: ProductModel) {
        products.add(0, product)
    }

    fun deleteProduct(productId: Long) {
        products.remove(products.find { it.id == productId })
    }

    fun setPurchaseStatusToTrue(productId: Long) {
        products.first() { it.id == productId }.purchased = true
    }

    fun setPurchaseStatusToFalse(productId: Long) {
        products.first() { it.id == productId }.purchased = false
    }

}