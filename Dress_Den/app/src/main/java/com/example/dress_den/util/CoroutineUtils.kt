package com.example.dress_den.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object CoroutineUtils {
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun launchMain(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return mainScope.launch(context, start, block)
    }

    fun launchIO(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return ioScope.launch(context, start, block)
    }

    suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Main, block)
    }

    suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO, block)
    }

    fun <T> Flow<T>.flowOnMain(): Flow<T> = flowOn(Dispatchers.Main)

    fun <T> Flow<T>.flowOnIO(): Flow<T> = flowOn(Dispatchers.IO)

    fun CoroutineScope.launchWithCatch(
        context: CoroutineContext = EmptyCoroutineContext,
        onError: (Throwable) -> Unit = { it.printStackTrace() },
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(context + CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }) {
            block()
        }
    }

    suspend fun <T> retryWithDelay(
        times: Int = 3,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
        return block() // last attempt
    }

    fun <T> Flow<T>.retryWithDelay(
        times: Int = 3,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        predicate: (cause: Throwable) -> Boolean = { true }
    ): Flow<T> = retry(times) { cause ->
        if (predicate(cause)) {
            delay(initialDelay)
            true
        } else {
            false
        }
    }

    fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
        var lastEmissionTime = 0L
        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime >= windowDuration) {
                lastEmissionTime = currentTime
                emit(upstream)
            }
        }
    }

    fun <T> Flow<T>.throttleLast(windowDuration: Long): Flow<T> = flow {
        var lastValue: T? = null
        var lastEmissionTime = 0L
        
        collect { upstream ->
            lastValue = upstream
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime >= windowDuration) {
                lastEmissionTime = currentTime
                lastValue?.let { emit(it) }
            }
        }
    }

    fun <T> Flow<T>.debounce(timeoutMillis: Long): Flow<T> = flow {
        var lastValue: T? = null
        var debounceJob: Job? = null
        
        collect { value ->
            lastValue = value
            debounceJob?.cancel()
            debounceJob = coroutineScope {
                launch {
                    delay(timeoutMillis)
                    lastValue?.let { emit(it) }
                }
            }
        }
    }

    fun <T> Flow<T>.bufferTimeout(
        timeoutMillis: Long,
        bufferSize: Int = Int.MAX_VALUE
    ): Flow<List<T>> = flow {
        val buffer = mutableListOf<T>()
        var bufferJob: Job? = null
        
        fun emitBuffer() {
            if (buffer.isNotEmpty()) {
                emit(buffer.toList())
                buffer.clear()
            }
        }
        
        collect { value ->
            buffer.add(value)
            if (buffer.size >= bufferSize) {
                emitBuffer()
            }
            
            bufferJob?.cancel()
            bufferJob = coroutineScope {
                launch {
                    delay(timeoutMillis)
                    emitBuffer()
                }
            }
        }
        
        // Emit remaining items
        emitBuffer()
    }

    fun <T> Flow<T>.withPrevious(): Flow<Pair<T?, T>> = flow {
        var previous: T? = null
        collect { current ->
            emit(previous to current)
            previous = current
        }
    }

    fun <T> Flow<T>.withTimeout(timeout: Long): Flow<T> = flow {
        withTimeout(timeout) {
            collect { value ->
                emit(value)
            }
        }
    }

    fun <T> Flow<T>.onTimeout(timeout: Long, onTimeout: suspend () -> Unit): Flow<T> = flow {
        try {
            withTimeout(timeout) {
                collect { value ->
                    emit(value)
                }
            }
        } catch (e: TimeoutCancellationException) {
            onTimeout()
            throw e
        }
    }

    class CoroutineException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
