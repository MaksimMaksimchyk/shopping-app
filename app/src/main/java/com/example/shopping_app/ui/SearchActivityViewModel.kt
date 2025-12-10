package com.example.shopping_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_app.data.FakeDataBase
import com.example.shopping_app.data.LocalProductsDataSource
import com.example.shopping_app.data.ProductsRepositoryImpl
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

public class SearchActivityViewModel: ViewModel() {
    private val _allProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val allProducts = _allProducts.asStateFlow()
    private val _uiState = MutableStateFlow<UiState>(UiState.ShowResult())
    val uiState = _uiState.asStateFlow()
    val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<ProductModel>> = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            fakeLoadFlow(query).catch { _ ->
                _uiState.value = UiState.Error()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun fakeLoadFlow(query: String): Flow<List<ProductModel>> = flow {
        if (!query.isBlank()) {
            _uiState.value = UiState.Loading()
            delay(1000L)
            if (Random.nextInt(0, 100) > 80) {
                throw Exception()
            }
            val currentAllItems = _allProducts.value
            val results = if (query.isBlank()) {
                currentAllItems
            } else {
                currentAllItems.filter { item ->
                    item.name.contains(query, ignoreCase = true)
                }
            }
            emit(results)
            if (results.isEmpty()) _uiState.value = UiState.EmptyResult() else _uiState.value =
                UiState.ShowResult()
        } else emit(_allProducts.value)
    }

    fun changePurchaseStatus(product: ProductModel, isChecked: Boolean) {
        productsInteractor.changePurchaseStatus(product, isChecked)
        loadProducts()
    }

    sealed interface UiState {
        class Loading() : UiState
        class Error() : UiState
        class EmptyResult() : UiState
        class ShowResult() : UiState
    }
}
