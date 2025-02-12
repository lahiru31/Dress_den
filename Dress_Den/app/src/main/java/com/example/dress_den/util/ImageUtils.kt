package com.example.dress_den.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.dress_den.R
import java.io.File

object ImageUtils {

    fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.placeholder_image,
        error: Int = R.drawable.error_image,
        cornerRadius: Int = 0,
        onLoadingComplete: ((Boolean) -> Unit)? = null
    ) {
        val requestOptions = RequestOptions()
            .placeholder(placeholder)
            .error(error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        if (cornerRadius > 0) {
            requestOptions.transform(CenterCrop(), RoundedCorners(cornerRadius))
        }

        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadingComplete?.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadingComplete?.invoke(true)
                    return false
                }
            })
            .into(imageView)
    }

    fun loadCircularImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.placeholder_avatar,
        error: Int = R.drawable.error_avatar
    ) {
        Glide.with(context)
            .load(url)
            .apply(
                RequestOptions()
                    .placeholder(placeholder)
                    .error(error)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun loadLocalImage(
        context: Context,
        imageView: ImageView,
        uri: Uri,
        placeholder: Int = R.drawable.placeholder_image,
        error: Int = R.drawable.error_image
    ) {
        Glide.with(context)
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(placeholder)
                    .error(error)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun clearCache(context: Context) {
        Glide.get(context).clearMemory()
        Thread {
            Glide.get(context).clearDiskCache()
        }.start()
    }

    fun preloadImage(context: Context, url: String?) {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .preload()
    }

    fun getDrawableFromResource(context: Context, resourceId: Int): Drawable? {
        return ContextCompat.getDrawable(context, resourceId)
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        return try {
            val file = File(context.cacheDir, fileName)
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun calculateImageDimensions(
        originalWidth: Int,
        originalHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Pair<Int, Int> {
        var width = originalWidth
        var height = originalHeight

        if (width > maxWidth) {
            val ratio = maxWidth.toFloat() / width
            width = maxWidth
            height = (height * ratio).toInt()
        }

        if (height > maxHeight) {
            val ratio = maxHeight.toFloat() / height
            height = maxHeight
            width = (width * ratio).toInt()
        }

        return Pair(width, height)
    }
}
