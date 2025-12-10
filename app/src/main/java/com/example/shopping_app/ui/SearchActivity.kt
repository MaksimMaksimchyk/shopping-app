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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping_app.databinding.ActivitySearchBinding
import com.example.shopping_app.ui.adapter.ProductsAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchActivityViewModel by viewModels()
    private lateinit var adapter: ProductsAdapter
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupObservers()
        setupRecyclerView()
        startSearch()

    }

    private fun setupListeners() {

        binding.searhEditText.addTextChangedListener { editText ->
            viewModel.updateSearchQuery(editText.toString())
        }

        binding.searchButton.setOnClickListener {
            hideKeyboard()
            binding.searhEditText.clearFocus()
        }

        binding.searhEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchButton.performClick()
                true
            } else {
                false
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun setupObservers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SearchActivityViewModel.UiState.EmptyResult -> showEmptyResult()
                        is SearchActivityViewModel.UiState.Error -> showError()
                        is SearchActivityViewModel.UiState.Loading -> showLoading()
                        is SearchActivityViewModel.UiState.ShowResult -> showResults()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredProducts.collect { productsList ->
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

    private fun showResults() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.centerText.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showEmptyResult() {
        binding.recyclerView.visibility = View.INVISIBLE
        binding.centerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.centerText.text = "Ничего не найдено"
    }

    private fun showError() {
        binding.recyclerView.visibility = View.INVISIBLE
        binding.centerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.centerText.text = "Ошибка"
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.INVISIBLE
        binding.centerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.centerText.text = "Поиск..."
    }

    private fun startSearch() {
        binding.linearSearchLayout.visibility = View.VISIBLE
        binding.searhEditText.requestFocus()
        WindowCompat.getInsetsController(window, binding.searhEditText)
            .show(WindowInsetsCompat.Type.ime())

    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, binding.searchButton)
            .hide(WindowInsetsCompat.Type.ime())
    }
}