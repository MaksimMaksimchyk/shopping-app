package com.example.shopping_app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shopping_app.data.LocalProductsDataSource
import com.example.shopping_app.data.ProductsRepositoryImpl
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> = _products

    private val _uiState = MutableStateFlow<UiState>(UiState.BaseState())
    val uiState = _uiState.asStateFlow()

    private val productsInteractor: ProductsInteractor by lazy {
        val dataSource = LocalProductsDataSource()
        val repository = ProductsRepositoryImpl(dataSource)
        ProductsInteractor(repository)
    }

    init {
        loadProducts()
    }

    fun loadProducts() {
        val productsList = productsInteractor.getProducts()
        _products.value = productsList
    }

    fun addProduct(name: String) {
        if (name.isBlank()) return
        productsInteractor.addProduct(name)
        loadProducts()
        _uiState.value = UiState.BaseState()
    }

    fun deleteProduct(product: ProductModel) {
        productsInteractor.deleteProduct(product.id)
        loadProducts()
    }

    fun changePurchaseStatus(product: ProductModel) {
        productsInteractor.changePurchaseStatus(product)
        loadProducts()
    }

    fun startInputNewItem() {
        _uiState.value = UiState.InputNewItemState()
    }

    fun startSearch() {
        _uiState.value = UiState.SearhItems()
    }

    fun backToBase(){
        _uiState.value = UiState.BaseState()
    }

    sealed interface UiState {
        class BaseState() : UiState
        class InputNewItemState() : UiState
        class SearhItems() : UiState
    }

}