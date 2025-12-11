package com.example.shopping_app

import com.example.shopping_app.data.LocalProductsDataSource
import com.example.shopping_app.data.ProductsRepositoryImpl
import com.example.shopping_app.domain.ProductsInteractor
import com.example.shopping_app.domain.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsModule {
    @Binds
    abstract fun bindsProdutcsRepo(produts: ProductsRepositoryImpl): ProductsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PrudutsModule {
    @Provides
    fun providesInteractor(repo: ProductsRepository): ProductsInteractor {
        return ProductsInteractor(repo)
    }
    @Provides
    fun providesDataSource(): LocalProductsDataSource {
        return LocalProductsDataSource()
    }

}