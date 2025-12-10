package com.example.shopping_app.domain

class ProductsInteractor(private val productsRepository: ProductsRepository) {

    fun getProducts(): List<ProductModel> = productsRepository.getProducts()

    fun addProduct(name: String) {
        val product = ProductModel(name = name)
        productsRepository.addProduct(product)
    }

    fun deleteProduct(id: Long) {
        productsRepository.deleteProduct(id)
    }

    fun changePurchaseStatus(product: ProductModel, isChecked: Boolean) {
        if (isChecked == true) productsRepository.setPurchaseStatusToTrue(product.id)
        else productsRepository.setPurchaseStatusToFalse(product.id)
    }

}