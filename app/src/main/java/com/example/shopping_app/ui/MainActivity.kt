package com.example.shopping_app.ui

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
import kotlinx.coroutines.launch
import kotlin.getValue

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

    private fun setupListeners() {

        binding.buttonAdd.setOnClickListener {
            val productName = binding.inputProduct.text.toString().trim()
            viewModel.addProduct(productName)
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
            viewModel.startInputNewItem()
        }

        binding.floatingSearchButton.setOnClickListener {
            viewModel.startSearch()
        }

        binding.searchButton.setOnClickListener {
            viewModel.backToBase()
        }

        binding.searhEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchButton.performClick()
                true
            } else {
                false
            }
        }


    }

    private fun setupObservers() {

        viewModel.products.observe(this) { products ->
            adapter.updateList(products)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainActivityViewModel.UiState.BaseState -> showBaseState()
                        is MainActivityViewModel.UiState.InputNewItemState -> showInputNewItemState()
                        is MainActivityViewModel.UiState.SearhItems -> showSearchItemsState()
                    }
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
        binding.linearInputLayout.visibility = View.GONE
        binding.linearSearchLayout.visibility = View.GONE
        hideKeyboard()
        binding.inputProduct.text.clear()
        binding.inputProduct.clearFocus()
        binding.searhEditText.text.clear()
        binding.searhEditText.clearFocus()
        binding.recyclerView.smoothScrollToPosition(0)
    }

    private fun showInputNewItemState() {
        binding.linearInputLayout.visibility = View.VISIBLE
        binding.inputProduct.requestFocus()
        WindowCompat.getInsetsController(window, binding.inputProduct)
            .show(WindowInsetsCompat.Type.ime())
    }

    private fun showSearchItemsState() {
        binding.linearSearchLayout.visibility = View.VISIBLE
        binding.searhEditText.requestFocus()
        WindowCompat.getInsetsController(window, binding.searhEditText)
            .show(WindowInsetsCompat.Type.ime())
    }


    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, binding.buttonAdd)
            .hide(WindowInsetsCompat.Type.ime())
        WindowCompat.getInsetsController(window, binding.searchButton)
            .hide(WindowInsetsCompat.Type.ime())
    }
}