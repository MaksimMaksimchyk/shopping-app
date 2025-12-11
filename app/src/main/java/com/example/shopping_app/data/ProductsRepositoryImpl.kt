package com.example.shopping_app.data

import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.domain.ProductsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepositoryImpl @Inject constructor(private val localProductsDataSource: LocalProductsDataSource) :
    ProductsRepository {

    override fun getProducts(): List<ProductModel> {
        return localProductsDataSource.getProducts()
    }

    override fun addProduct(product: ProductModel) {
        localProductsDataSource.addProduct(product)
    }

    override fun deleteProduct(id: Long) {
        localProductsDataSource.deleteProduct(id)
    }

    override fun setPurchaseStatusToTrue(id: Long) {
        localProductsDataSource.setPurchaseStatusToTrue(id)
    }

    override fun setPurchaseStatusToFalse(id: Long) {
        localProductsDataSource.setPurchaseStatusToFalse(id)
    }
}