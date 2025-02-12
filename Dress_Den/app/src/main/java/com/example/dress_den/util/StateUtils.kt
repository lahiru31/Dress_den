package com.example.dress_den.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object StateUtils {

    sealed class UiState<out T> {
        object Loading : UiState<Nothing>()
        object Empty : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String, val code: String? = null) : UiState<Nothing>()

        fun isLoading() = this is Loading
        fun isEmpty() = this is Empty
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

    sealed class ViewState {
        object Idle : ViewState()
        object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data class Success(val message: String? = null) : ViewState()
    }

    sealed class ValidationState {
        object Valid : ValidationState()
        data class Invalid(val message: String) : ValidationState()
        object None : ValidationState()
    }

    sealed class NetworkState {
        object Connected : NetworkState()
        object Disconnected : NetworkState()
        data class Error(val message: String) : NetworkState()
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class LoadingState {
        object Loading : LoadingState()
        object NotLoading : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    class StateHolder<T>(initialValue: T) {
        private val _state = MutableStateFlow(initialValue)
        val state: StateFlow<T> = _state.asStateFlow()

        fun update(newValue: T) {
            _state.value = newValue
        }

        fun updateIf(predicate: (T) -> Boolean, newValue: T) {
            if (predicate(_state.value)) {
                _state.value = newValue
            }
        }

        fun updateWith(transform: (T) -> T) {
            _state.update(transform)
        }

        fun getCurrentValue(): T = _state.value
    }

    class UiStateHolder<T> {
        private val _state = MutableStateFlow<UiState<T>>(UiState.Loading)
        val state: StateFlow<UiState<T>> = _state.asStateFlow()

        fun setLoading() {
            _state.value = UiState.Loading
        }

        fun setEmpty() {
            _state.value = UiState.Empty
        }

        fun setSuccess(data: T) {
            _state.value = UiState.Success(data)
        }

        fun setError(message: String, code: String? = null) {
            _state.value = UiState.Error(message, code)
        }

        fun getCurrentState(): UiState<T> = _state.value

        fun updateIfSuccess(transform: (T) -> T) {
            val currentState = _state.value
            if (currentState is UiState.Success) {
                _state.value = UiState.Success(transform(currentState.data))
            }
        }
    }

    class ViewStateHolder {
        private val _state = MutableStateFlow<ViewState>(ViewState.Idle)
        val state: StateFlow<ViewState> = _state.asStateFlow()

        fun setIdle() {
            _state.value = ViewState.Idle
        }

        fun setLoading() {
            _state.value = ViewState.Loading
        }

        fun setError(message: String) {
            _state.value = ViewState.Error(message)
        }

        fun setSuccess(message: String? = null) {
            _state.value = ViewState.Success(message)
        }
    }

    class ValidationStateHolder {
        private val _state = MutableStateFlow<ValidationState>(ValidationState.None)
        val state: StateFlow<ValidationState> = _state.asStateFlow()

        fun setValid() {
            _state.value = ValidationState.Valid
        }

        fun setInvalid(message: String) {
            _state.value = ValidationState.Invalid(message)
        }

        fun reset() {
            _state.value = ValidationState.None
        }

        fun isValid(): Boolean = _state.value is ValidationState.Valid
    }

    class FormState {
        private val validationStates = mutableMapOf<String, ValidationState>()
        private val _isValid = MutableStateFlow(false)
        val isValid: StateFlow<Boolean> = _isValid.asStateFlow()

        fun updateField(fieldName: String, state: ValidationState) {
            validationStates[fieldName] = state
            updateValidState()
        }

        fun getFieldState(fieldName: String): ValidationState {
            return validationStates[fieldName] ?: ValidationState.None
        }

        fun clearField(fieldName: String) {
            validationStates.remove(fieldName)
            updateValidState()
        }

        fun reset() {
            validationStates.clear()
            _isValid.value = false
        }

        private fun updateValidState() {
            _isValid.value = validationStates.values.all { it is ValidationState.Valid }
        }

        fun getInvalidFields(): List<String> {
            return validationStates.filter { it.value is ValidationState.Invalid }
                .map { it.key }
        }
    }

    class LoadingStateHolder {
        private val _state = MutableStateFlow<LoadingState>(LoadingState.NotLoading)
        val state: StateFlow<LoadingState> = _state.asStateFlow()

        fun startLoading() {
            _state.value = LoadingState.Loading
        }

        fun stopLoading() {
            _state.value = LoadingState.NotLoading
        }

        fun setError(message: String) {
            _state.value = LoadingState.Error(message)
        }

        fun isLoading(): Boolean = _state.value is LoadingState.Loading
    }

    class StateException(message: String) : Exception(message)
}
