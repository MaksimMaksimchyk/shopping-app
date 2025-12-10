package com.example.shopping_app.data

import com.example.shopping_app.domain.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow

object FakeDataBase {
    val products = mutableListOf<ProductModel>(
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

}