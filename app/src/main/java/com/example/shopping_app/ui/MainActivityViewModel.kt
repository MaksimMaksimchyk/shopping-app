package com.example.shopping_app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.shopping_app.data.LocalProductsDataSource
import com.example.shopping_app.data.ProductsRepositoryImpl
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsInteractor
import com.example.shopping_app.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val productsInteractor: ProductsInteractor
) : ViewModel() {
    private val _allProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val allProducts = _allProducts.asStateFlow()

//    private val productsInteractor: ProductsInteractor by lazy {
//        val dataSource = LocalProductsDataSource()
//        val repository = ProductsRepositoryImpl(dataSource)
//        ProductsInteractor(repository)
//    }

    init {
        loadProducts()
        Log.i("testMain", productsInteractor.toString())
    }

    fun loadProducts() {
        _allProducts.value = productsInteractor.getProducts()
    }

    fun addProduct(name: String) {
        if (name.isBlank()) return
        productsInteractor.addProduct(name)
        loadProducts()
        Log.i("testMain", productsInteractor.toString())
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