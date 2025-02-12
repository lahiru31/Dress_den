package com.example.dress_den.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    protected abstract fun createBinding(inflater: LayoutInflater): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = createBinding(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
        setupListeners()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected open fun setupViews() {}
    
    protected open fun setupObservers() {
        // Observe common ViewModel states
        observeLoading()
        observeError()
        observeToast()
    }
    
    protected open fun setupListeners() {}
    
    protected open fun initData() {}

    private fun observeLoading() {
        collectFlow(viewModel.loading) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }
    }

    private fun observeError() {
        collectFlow(viewModel.error) { message ->
            showError(message)
        }
    }

    private fun observeToast() {
        collectFlow(viewModel.toast) { message ->
            showToast(message)
        }
    }

    protected fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).apply {
            setAction("OK") { dismiss() }
            show()
        }
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected open fun showLoading() {
        // Override in subclasses to show loading indicator
    }

    protected open fun hideLoading() {
        // Override in subclasses to hide loading indicator
    }

    protected fun <T> collectFlow(flow: Flow<T>, action: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { action(it) }
            }
        }
    }

    protected fun <T> collectLatestFlow(flow: Flow<T>, action: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { action(it) }
            }
        }
    }

    protected fun <T> BaseViewModel.UiState<T>.handleState(
        onSuccess: (T) -> Unit,
        onError: ((String) -> Unit)? = null,
        onLoading: (() -> Unit)? = null
    ) {
        when (this) {
            is BaseViewModel.UiState.Success -> onSuccess(data)
            is BaseViewModel.UiState.Error -> onError?.invoke(message) ?: showError(message)
            is BaseViewModel.UiState.Loading -> onLoading?.invoke() ?: showLoading()
        }
    }

    protected fun hideKeyboard() {
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        currentFocus?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    protected fun showKeyboard(view: android.view.View) {
        view.requestFocus()
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }

    protected fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    protected fun setLightStatusBar() {
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    protected fun clearLightStatusBar() {
        window.decorView.systemUiVisibility = 0
    }

    protected fun setStatusBarColor(color: Int) {
        window.statusBarColor = color
    }

    protected fun setNavigationBarColor(color: Int) {
        window.navigationBarColor = color
    }
}
