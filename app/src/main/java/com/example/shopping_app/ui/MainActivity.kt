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
import com.example.shopping_app.databinding.ActivityMainBinding
import com.example.shopping_app.ui.adapter.ProductsAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var adapter: ProductsAdapter
    private lateinit var binding: ActivityMainBinding
    private var collectQueryResults: Job? = null

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
            startSearch()
        }

        binding.searhEditText.addTextChangedListener { editText ->
            viewModel.updateSearchQuery(editText.toString())
        }

        binding.searchButton.setOnClickListener {
            hideKeyboard()
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
            viewModel.backHome()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainActivityViewModel.UiState.BaseState -> showBaseState()
                        is MainActivityViewModel.UiState.EmptyResult -> showEmptyResult()
                        is MainActivityViewModel.UiState.Error -> showError()
                        is MainActivityViewModel.UiState.Loading -> showLoading()
                        is MainActivityViewModel.UiState.ShowResult -> showResults()
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
        binding.backButton.visibility = View.GONE
        adapter.updateList(viewModel.allProducts.value)
        binding.floatingAddButton.visibility = View.VISIBLE
        binding.floatingSearchButton.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.linearInputLayout.visibility = View.GONE
        binding.linearSearchLayout.visibility = View.GONE
        hideKeyboard()
        binding.searhEditText.setText("")
        binding.centerText.visibility = View.INVISIBLE
    }

    private fun showResults() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.centerText.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.floatingAddButton.visibility = View.INVISIBLE
        binding.floatingSearchButton.visibility = View.INVISIBLE
    }

    private fun showEmptyResult() {
        binding.recyclerView.visibility = View.INVISIBLE
        binding.centerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.centerText.text = "Ничего не найдено"
        binding.floatingAddButton.visibility = View.INVISIBLE
        binding.floatingSearchButton.visibility = View.INVISIBLE
    }

    private fun showError() {
        binding.recyclerView.visibility = View.INVISIBLE
        binding.centerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.centerText.text = "Ошибка"
        binding.floatingAddButton.visibility = View.INVISIBLE
        binding.floatingSearchButton.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.backButton.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.INVISIBLE
        binding.centerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.centerText.text = "Поиск..."
    }

    private fun startInput() {
        adapter.updateList(viewModel.allProducts.value)
        binding.linearSearchLayout.visibility = View.GONE
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

    private fun startSearch() {
        binding.linearSearchLayout.visibility = View.VISIBLE
        binding.linearInputLayout.visibility = View.GONE
        binding.searhEditText.requestFocus()
        WindowCompat.getInsetsController(window, binding.searhEditText)
            .show(WindowInsetsCompat.Type.ime())

        //По нажатию на кнопку запускаем джобу на коллект результатов поиска
        if (collectQueryResults == null) collectQueryResults = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredProducts.collect { productsList ->
                    if (viewModel.uiState.value !is MainActivityViewModel.UiState.BaseState) adapter.updateList(
                        productsList
                    )
                }
            }
        }
    }


    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, binding.buttonAdd)
            .hide(WindowInsetsCompat.Type.ime())
        WindowCompat.getInsetsController(window, binding.searchButton)
            .hide(WindowInsetsCompat.Type.ime())
    }
}