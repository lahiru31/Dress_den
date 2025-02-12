package com.example.dress_den.util

import android.util.Log
import com.example.dress_den.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.json.JSONArray
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter

object LogUtils {
    private const val MAX_LOG_LENGTH = 4000
    private const val MAX_TAG_LENGTH = 23
    private const val JSON_INDENT = 2

    private var isLoggingEnabled = BuildConfig.DEBUG
    private var crashlyticsEnabled = !BuildConfig.DEBUG

    fun init(enableLogging: Boolean = BuildConfig.DEBUG, enableCrashlytics: Boolean = !BuildConfig.DEBUG) {
        isLoggingEnabled = enableLogging
        crashlyticsEnabled = enableCrashlytics
        
        if (crashlyticsEnabled) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }
    }

    fun v(tag: String, message: String) {
        if (isLoggingEnabled) {
            log(Log.VERBOSE, tag, message)
        }
    }

    fun d(tag: String, message: String) {
        if (isLoggingEnabled) {
            log(Log.DEBUG, tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (isLoggingEnabled) {
            log(Log.INFO, tag, message)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            log(Log.WARN, tag, message, throwable)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            log(Log.ERROR, tag, message, throwable)
        }
        if (crashlyticsEnabled) {
            logToCrashlytics(tag, message, throwable)
        }
    }

    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            log(Log.ASSERT, tag, message, throwable)
        }
        if (crashlyticsEnabled) {
            logToCrashlytics(tag, message, throwable)
        }
    }

    private fun log(priority: Int, tag: String, message: String, throwable: Throwable? = null) {
        val safeTag = tag.take(MAX_TAG_LENGTH)
        val fullMessage = if (throwable != null) {
            "$message\n${getStackTraceString(throwable)}"
        } else {
            message
        }

        // Split the message if it's too long
        if (fullMessage.length < MAX_LOG_LENGTH) {
            Log.println(priority, safeTag, fullMessage)
        } else {
            // Split the message into chunks
            var i = 0
            while (i < fullMessage.length) {
                val end = (i + MAX_LOG_LENGTH).coerceAtMost(fullMessage.length)
                Log.println(priority, safeTag, fullMessage.substring(i, end))
                i = end
            }
        }
    }

    private fun logToCrashlytics(tag: String, message: String, throwable: Throwable? = null) {
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("tag", tag)
            log("$tag: $message")
            throwable?.let { recordException(it) }
        }
    }

    fun logJson(tag: String, json: String) {
        if (!isLoggingEnabled) return

        try {
            val trimmedJson = json.trim()
            if (trimmedJson.startsWith("{")) {
                val jsonObject = JSONObject(trimmedJson)
                d(tag, jsonObject.toString(JSON_INDENT))
            } else if (trimmedJson.startsWith("[")) {
                val jsonArray = JSONArray(trimmedJson)
                d(tag, jsonArray.toString(JSON_INDENT))
            } else {
                e(tag, "Invalid JSON format: $json")
            }
        } catch (e: Exception) {
            e(tag, "Error parsing JSON: $json", e)
        }
    }

    fun logMethod(tag: String = getCallerClassName()) {
        if (!isLoggingEnabled) return
        
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 4) {
            val element = stackTrace[3]
            d(tag, "${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
        }
    }

    private fun getCallerClassName(): String {
        val stackTrace = Thread.currentThread().stackTrace
        var callerClassName = "Unknown"
        
        for (i in 3 until stackTrace.size) {
            val className = stackTrace[i].className
            if (!className.contains("LogUtils")) {
                callerClassName = className.substringAfterLast('.')
                break
            }
        }
        
        return callerClassName
    }

    private fun getStackTraceString(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }

    fun addBreadcrumb(message: String, category: String? = null) {
        if (crashlyticsEnabled) {
            FirebaseCrashlytics.getInstance().log("$category: $message")
        }
    }

    fun setUserIdentifier(userId: String) {
        if (crashlyticsEnabled) {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        }
    }

    fun setCustomKey(key: String, value: String) {
        if (crashlyticsEnabled) {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        }
    }

    fun logNetworkCall(
        url: String,
        method: String,
        requestHeaders: Map<String, String>? = null,
        requestBody: String? = null,
        responseCode: Int? = null,
        responseBody: String? = null,
        error: Throwable? = null
    ) {
        if (!isLoggingEnabled) return

        val tag = "NetworkCall"
        val sb = StringBuilder().apply {
            appendLine("URL: $url")
            appendLine("Method: $method")
            
            requestHeaders?.let {
                appendLine("Request Headers:")
                it.forEach { (key, value) ->
                    appendLine("  $key: $value")
                }
            }
            
            requestBody?.let {
                appendLine("Request Body:")
                appendLine(it)
            }
            
            responseCode?.let {
                appendLine("Response Code: $it")
            }
            
            responseBody?.let {
                appendLine("Response Body:")
                appendLine(it)
            }
        }

        if (error != null) {
            e(tag, sb.toString(), error)
        } else {
            d(tag, sb.toString())
        }
    }

    fun logLifecycle(tag: String, message: String) {
        if (isLoggingEnabled) {
            v("Lifecycle_$tag", message)
        }
    }

    class Logger(private val tag: String) {
        fun v(message: String) = LogUtils.v(tag, message)
        fun d(message: String) = LogUtils.d(tag, message)
        fun i(message: String) = LogUtils.i(tag, message)
        fun w(message: String, throwable: Throwable? = null) = LogUtils.w(tag, message, throwable)
        fun e(message: String, throwable: Throwable? = null) = LogUtils.e(tag, message, throwable)
        fun wtf(message: String, throwable: Throwable? = null) = LogUtils.wtf(tag, message, throwable)
    }
}
