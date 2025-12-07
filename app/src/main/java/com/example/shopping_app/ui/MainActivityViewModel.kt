package com.example.shopping_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping_app.data.LocalProductsDataSource
import com.example.shopping_app.data.ProductsRepositoryImpl
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel : ViewModel() {
    private val _allProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    private val _uiState = MutableStateFlow<UiState>(UiState.BaseState())
    val uiState = _uiState.asStateFlow()
    val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _queryCount = 0


    val filteredProducts: StateFlow<List<ProductModel>> = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            fakeLoadFlow(query)
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
        _uiState.value = UiState.Loading()
        if (_queryCount != 0) delay(1000L)
        _queryCount++
        val currentAllItems = _allProducts.value
        val results = if (query.isBlank()) {
            currentAllItems
        } else {
            currentAllItems.filter { item ->
                item.name.contains(query, ignoreCase = true)
            }
        }
        emit(results)
        if (results.isEmpty()) _uiState.value = UiState.EmptyResults() else _uiState.value =
            UiState.ShowSearchResults()
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
        _uiState.value = UiState.InputingSearchQuery()
    }

    fun viewSearchResults() {
        _uiState.value = UiState.ShowSearchResults()
    }

    fun backHome() {
        _uiState.value = UiState.BaseState()
    }

    sealed interface UiState {
        class BaseState() : UiState
        class InputNewItemState() : UiState
        class InputingSearchQuery() : UiState
        class ShowSearchResults() : UiState
        class Loading() : UiState
        class EmptyResults() : UiState
        class Error() : UiState
    }

}