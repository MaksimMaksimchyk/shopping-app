package com.example.shopping_app.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping_app.databinding.ActivityMainBinding
import com.example.shopping_app.ui.adapter.ProductsAdapter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import kotlin.getValue

@HiltAndroidApp
class ShoppingApp: Application()

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var adapter: ProductsAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupObservers()
        setupRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }


    private fun setupListeners() {

        binding.buttonAdd.setOnClickListener {
            val productName = binding.inputProduct.text.toString().trim()
            viewModel.addProduct(productName)
            stopInput()
        }

        binding.inputProduct.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.buttonAdd.performClick()
                true
            } else {
                false
            }
        }

        binding.floatingAddButton.setOnClickListener {
            startInput()
        }

        binding.floatingSearchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

    }

    private fun setupObservers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allProducts.collect { productsList ->
                    adapter.updateList(productsList)
                }
            }
        }

    }

    private fun setupRecyclerView() {
        adapter = ProductsAdapter(viewModel::changePurchaseStatus)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showBaseState() {
        adapter.updateList(viewModel.allProducts.value)
        binding.linearInputLayout.visibility = View.GONE
        hideKeyboard()
    }


    private fun startInput() {
        adapter.updateList(viewModel.allProducts.value)
        binding.linearInputLayout.visibility = View.VISIBLE
        binding.inputProduct.requestFocus()
        WindowCompat.getInsetsController(window, binding.inputProduct)
            .show(WindowInsetsCompat.Type.ime())
    }

    private fun stopInput() {
        binding.inputProduct.setText("")
        binding.linearInputLayout.visibility = View.GONE
        binding.inputProduct.clearFocus()
        hideKeyboard()
        binding.recyclerView.smoothScrollToPosition(0)
    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, binding.buttonAdd)
            .hide(WindowInsetsCompat.Type.ime())
    }
}