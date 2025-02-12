package com.example.dress_den.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object StorageUtils {
    private const val IMAGES_FOLDER = "images"
    private const val TEMP_FOLDER = "temp"
    private const val DOCUMENTS_FOLDER = "documents"
    private const val CACHE_FOLDER = "cache"
    private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    private const val JPEG_QUALITY = 90

    fun createImageFile(context: Context, prefix: String = "IMG"): File {
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val fileName = "${prefix}_${timeStamp}.jpg"
        return File(getImagesDirectory(context), fileName)
    }

    fun createTempFile(context: Context, prefix: String, suffix: String): File {
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val fileName = "${prefix}_${timeStamp}${suffix}"
        return File(getTempDirectory(context), fileName)
    }

    fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun copyFile(source: File, destination: File): Boolean {
        return try {
            source.inputStream().use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun deleteFile(file: File): Boolean {
        return if (file.exists()) {
            file.delete()
        } else false
    }

    fun clearDirectory(directory: File) {
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    clearDirectory(file)
                }
                file.delete()
            }
        }
    }

    fun getImagesDirectory(context: Context): File {
        return getOrCreateDirectory(context, IMAGES_FOLDER)
    }

    fun getTempDirectory(context: Context): File {
        return getOrCreateDirectory(context, TEMP_FOLDER)
    }

    fun getDocumentsDirectory(context: Context): File {
        return getOrCreateDirectory(context, DOCUMENTS_FOLDER)
    }

    fun getCacheDirectory(context: Context): File {
        return getOrCreateDirectory(context, CACHE_FOLDER)
    }

    private fun getOrCreateDirectory(context: Context, folderName: String): File {
        val directory = File(context.getExternalFilesDir(null), folderName)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    fun getFileSize(file: File): Long {
        return if (file.exists() && file.isFile) {
            file.length()
        } else 0
    }

    fun getFormattedFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format(
            "%.1f %s",
            size / Math.pow(1024.0, digitGroups.toDouble()),
            units[digitGroups]
        )
    }

    fun getMimeType(file: File): String? {
        val extension = file.extension
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "txt" -> "text/plain"
            else -> null
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = getFileUri(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share file"))
    }

    fun readTextFile(file: File): String {
        return file.readText()
    }

    fun writeTextFile(file: File, content: String) {
        file.writeText(content)
    }

    fun appendToFile(file: File, content: String) {
        file.appendText(content)
    }

    fun createNoMediaFile(directory: File) {
        File(directory, ".nomedia").createNewFile()
    }

    fun isImageFile(file: File): Boolean {
        return getMimeType(file)?.startsWith("image/") == true
    }

    fun isDocumentFile(file: File): Boolean {
        val mimeType = getMimeType(file)
        return mimeType?.startsWith("application/") == true ||
               mimeType == "text/plain"
    }

    fun clearCache(context: Context) {
        clearDirectory(context.cacheDir)
        context.externalCacheDir?.let { clearDirectory(it) }
    }

    fun getTotalStorageSize(directory: File): Long {
        var size: Long = 0
        directory.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                getTotalStorageSize(file)
            } else {
                file.length()
            }
        }
        return size
    }

    class StorageException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
