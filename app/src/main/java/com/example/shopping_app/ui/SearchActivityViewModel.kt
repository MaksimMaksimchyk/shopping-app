package com.example.shopping_app.ui

import androidx.lifecycle.ViewModel
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
public class SearchActivityViewModel @Inject constructor(
    val productsInteractor: ProductsInteractor
) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _allProducts = BehaviorSubject.createDefault<List<ProductModel>>(emptyList())
    private val _uiState = BehaviorSubject.createDefault<UiState>(UiState.ShowResult())
    private val _searchQuery = BehaviorSubject.createDefault("")

    val allProducts: Observable<List<ProductModel>> = _allProducts.hide()
    val uiState: Observable<UiState> = _uiState.hide()
    val searchQuery: Observable<String> = _searchQuery.hide()

    val filteredProducts: Observable<List<ProductModel>> = _searchQuery
        .debounce(300, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .switchMap { query ->
            fakeLoadObservable(query)
                .onErrorResumeNext {
                    _uiState.onNext(UiState.Error())
                    Observable.just(emptyList())
                }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    init {
        loadProducts()
        disposables.add(filteredProducts.subscribe())
    }

    fun loadProducts() {
        _allProducts.onNext(productsInteractor.getProducts())
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.onNext(query)
    }

    private fun fakeLoadObservable(query: String): Observable<List<ProductModel>> {

        if (query.isBlank()) {
            _uiState.onNext(UiState.ShowResult())
            return Observable.just(_allProducts.value ?: emptyList())
        }

        return Observable.create<List<ProductModel>> { emitter ->
            _uiState.onNext(UiState.Loading())

            if (Random.nextInt(0, 100) > 80) {
                if (!emitter.isDisposed) emitter.onError(Exception("Random Error"))
                return@create
            }

            val currentAllItems = _allProducts.value ?: emptyList()
            val results = currentAllItems.filter { it.name.contains(query, ignoreCase = true) }

            if (!emitter.isDisposed) {
                emitter.onNext(results)

                if (results.isEmpty()) _uiState.onNext(UiState.EmptyResult())
                else _uiState.onNext(UiState.ShowResult())

                emitter.onComplete()
            }
        }
            .delay(1000, TimeUnit.MILLISECONDS, Schedulers.io())
            .subscribeOn(Schedulers.io())
    }

    fun changePurchaseStatus(product: ProductModel, isChecked: Boolean) {
        productsInteractor.changePurchaseStatus(product, isChecked)
        loadProducts()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    sealed interface UiState {
        class Loading : UiState
        class Error : UiState
        class EmptyResult : UiState
        class ShowResult : UiState
    }
}