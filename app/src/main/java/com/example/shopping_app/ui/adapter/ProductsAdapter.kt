package com.example.shopping_app.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping_app.databinding.ItemProductBinding
import com.example.shopping_app.domain.ProductModel
import com.example.shopping_app.ui.MainActivityViewModel
import com.example.shopping_app.ui.adapter.ProductsDiffUtilCallback

class ProductsAdapter(private val onCheckedChange: (ProductModel) -> Unit) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    private var products = listOf<ProductModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel) {
            binding.productName.text = product.name
            binding.checkbox.isChecked = product.purchased
            updateStrikeThrough(product.purchased)

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(product)
                updateStrikeThrough(product.purchased)
            }
        }

        /**
         *Зачёркнутый текст
         */
        private fun updateStrikeThrough(isPurchased: Boolean) {
            if (isPurchased) {
                binding.productName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.productName.paintFlags = 0
            }
        }

    }

    fun updateList(newProducts: List<ProductModel>) {
        val diffResult = DiffUtil.calculateDiff(ProductsDiffUtilCallback(products, newProducts))
        products = newProducts
        diffResult.dispatchUpdatesTo(this)
    }


}