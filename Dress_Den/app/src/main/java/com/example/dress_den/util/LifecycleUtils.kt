package com.example.dress_den.util

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object LifecycleUtils {

    fun <T> Flow<T>.observeWithLifecycle(
        lifecycleOwner: LifecycleOwner,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        action: suspend (T) -> Unit
    ): Job = lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            collect { action(it) }
        }
    }

    fun LifecycleOwner.launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }

    fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED, block)
        }
    }

    fun LifecycleOwner.launchWhenCreated(block: suspend CoroutineScope.() -> Unit): Job {
        return lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED, block)
        }
    }

    class LifecycleAwareObserver(
        private val onCreate: (() -> Unit)? = null,
        private val onStart: (() -> Unit)? = null,
        private val onResume: (() -> Unit)? = null,
        private val onPause: (() -> Unit)? = null,
        private val onStop: (() -> Unit)? = null,
        private val onDestroy: (() -> Unit)? = null
    ) : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            onCreate?.invoke()
        }

        override fun onStart(owner: LifecycleOwner) {
            onStart?.invoke()
        }

        override fun onResume(owner: LifecycleOwner) {
            onResume?.invoke()
        }

        override fun onPause(owner: LifecycleOwner) {
            onPause?.invoke()
        }

        override fun onStop(owner: LifecycleOwner) {
            onStop?.invoke()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            onDestroy?.invoke()
        }
    }

    class LifecycleAwareValue<T>(
        private val lifecycleOwner: LifecycleOwner,
        private val initialValue: T,
        private val onCreate: ((T) -> Unit)? = null,
        private val onStart: ((T) -> Unit)? = null,
        private val onResume: ((T) -> Unit)? = null,
        private val onPause: ((T) -> Unit)? = null,
        private val onStop: ((T) -> Unit)? = null,
        private val onDestroy: ((T) -> Unit)? = null
    ) {
        private var value: T = initialValue

        init {
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    onCreate?.invoke(value)
                }

                override fun onStart(owner: LifecycleOwner) {
                    onStart?.invoke(value)
                }

                override fun onResume(owner: LifecycleOwner) {
                    onResume?.invoke(value)
                }

                override fun onPause(owner: LifecycleOwner) {
                    onPause?.invoke(value)
                }

                override fun onStop(owner: LifecycleOwner) {
                    onStop?.invoke(value)
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    onDestroy?.invoke(value)
                }
            })
        }

        fun getValue(): T = value

        fun setValue(newValue: T) {
            value = newValue
            when (lifecycleOwner.lifecycle.currentState) {
                Lifecycle.State.CREATED -> onCreate?.invoke(value)
                Lifecycle.State.STARTED -> onStart?.invoke(value)
                Lifecycle.State.RESUMED -> onResume?.invoke(value)
                else -> {}
            }
        }
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer(value)
                removeObserver(this)
            }
        })
    }

    fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
        if (value != newValue) {
            value = newValue
        }
    }

    fun <T> MutableLiveData<T>.postValueIfNew(newValue: T) {
        if (value != newValue) {
            postValue(newValue)
        }
    }

    fun <T> LiveData<T>.requireValue(): T {
        return value ?: throw IllegalStateException("Value is null")
    }

    fun <T> LiveData<T>.filterNotNull(): LiveData<T> {
        return Transformations.map(this) { it!! }
    }

    fun <T> LiveData<List<T>>.filterList(predicate: (T) -> Boolean): LiveData<List<T>> {
        return Transformations.map(this) { list ->
            list.filter(predicate)
        }
    }

    fun <T, R> LiveData<T>.mapNotNull(transform: (T) -> R?): LiveData<R> {
        return MediatorLiveData<R>().apply {
            addSource(this@mapNotNull) { value ->
                transform(value)?.let { setValue(it) }
            }
        }
    }

    fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
        return MediatorLiveData<T>().apply {
            var lastValue: T? = null
            addSource(this@distinctUntilChanged) { value ->
                if (lastValue != value) {
                    lastValue = value
                    setValue(value)
                }
            }
        }
    }

    class LifecycleException(message: String) : Exception(message)
}
