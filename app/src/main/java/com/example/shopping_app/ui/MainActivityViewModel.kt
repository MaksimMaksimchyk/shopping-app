package com.example.shopping_app.ui

import androidx.lifecycle.ViewModel
import com.example.shopping_app.data.FakeDataBase
import com.example.shopping_app.data.LocalProductsDataSource
import com.example.shopping_app.data.ProductsRepositoryImpl
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
    private val _allProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val allProducts = _allProducts.asStateFlow()

    private val productsInteractor: ProductsInteractor by lazy {
        val dataSource = LocalProductsDataSource()
        val repository = ProductsRepositoryImpl(dataSource)
        ProductsInteractor(repository)
    }

    init {
        loadProducts()
    }

    fun loadProducts() {
        _allProducts.value = productsInteractor.getProducts()
    }

    fun addProduct(name: String) {
        if (name.isBlank()) return
        productsInteractor.addProduct(name)
        loadProducts()
    }

    fun deleteProduct(product: ProductModel) {
        productsInteractor.deleteProduct(product.id)
        loadProducts()
    }

    fun changePurchaseStatus(product: ProductModel, isChecked: Boolean) {
        productsInteractor.changePurchaseStatus(product, isChecked)
        loadProducts()
    }

}