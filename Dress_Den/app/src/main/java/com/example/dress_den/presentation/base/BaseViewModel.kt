package com.example.dress_den.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dress_den.data.remote.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    private val _toast = Channel<String>()
    val toast = _toast.receiveAsFlow()

    protected fun showLoading() {
        _loading.value = true
    }

    protected fun hideLoading() {
        _loading.value = false
    }

    protected fun showError(message: String) {
        viewModelScope.launch {
            _error.send(message)
        }
    }

    protected fun showToast(message: String) {
        viewModelScope.launch {
            _toast.send(message)
        }
    }

    protected fun <T> Flow<Resource<T>>.handleResource(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onSuccess: suspend (T) -> Unit,
        onError: (suspend (String) -> Unit)? = null
    ) {
        viewModelScope.launch(dispatcher) {
            onStart { showLoading() }
                .onCompletion { hideLoading() }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { onSuccess(it) }
                        }
                        is Resource.Error -> {
                            onError?.invoke(resource.message) ?: showError(resource.message)
                        }
                        is Resource.Loading -> {
                            // Loading is handled by onStart and onCompletion
                        }
                    }
                }
        }
    }

    protected fun <T> Flow<Resource<T>>.handleResourceWithState(
        state: MutableStateFlow<UiState<T>>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onSuccess: (suspend (T) -> Unit)? = null,
        onError: (suspend (String) -> Unit)? = null
    ) {
        viewModelScope.launch(dispatcher) {
            onStart {
                showLoading()
                state.value = UiState.Loading
            }
                .onCompletion { hideLoading() }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let {
                                state.value = UiState.Success(it)
                                onSuccess?.invoke(it)
                            }
                        }
                        is Resource.Error -> {
                            state.value = UiState.Error(resource.message)
                            onError?.invoke(resource.message) ?: showError(resource.message)
                        }
                        is Resource.Loading -> {
                            state.value = UiState.Loading
                        }
                    }
                }
        }
    }

    sealed class UiState<out T> {
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()

        fun isLoading() = this is Loading
        fun isSuccess() = this is Success
        fun isError() = this is Error

        fun getOrNull(): T? = when (this) {
            is Success -> data
            else -> null
        }

        fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
            is Success -> data
            else -> defaultValue
        }
    }

    protected fun <T> MutableStateFlow<UiState<T>>.updateData(transform: (T) -> T) {
        value = when (val currentState = value) {
            is UiState.Success -> UiState.Success(transform(currentState.data))
            else -> currentState
        }
    }

    protected fun <T> StateFlow<UiState<T>>.onSuccess(action: suspend (T) -> Unit): Job {
        return viewModelScope.launch {
            collect { state ->
                if (state is UiState.Success) {
                    action(state.data)
                }
            }
        }
    }

    protected fun <T> StateFlow<UiState<T>>.onError(action: suspend (String) -> Unit): Job {
        return viewModelScope.launch {
            collect { state ->
                if (state is UiState.Error) {
                    action(state.message)
                }
            }
        }
    }

    protected fun <T> StateFlow<UiState<T>>.onLoading(action: suspend () -> Unit): Job {
        return viewModelScope.launch {
            collect { state ->
                if (state is UiState.Loading) {
                    action()
                }
            }
        }
    }
}
