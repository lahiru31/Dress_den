package com.example.dress_den.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dress_den.R
import com.example.dress_den.presentation.main.MainActivity

object NotificationUtils {

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Orders Channel
            createNotificationChannel(
                notificationManager,
                Constants.CHANNEL_ORDERS,
                context.getString(R.string.notification_channel_name),
                context.getString(R.string.notification_channel_description),
                NotificationManager.IMPORTANCE_HIGH
            )

            // Promotions Channel
            createNotificationChannel(
                notificationManager,
                Constants.CHANNEL_PROMOTIONS,
                "Promotions",
                "Receive updates about offers and promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // General Channel
            createNotificationChannel(
                notificationManager,
                Constants.CHANNEL_GENERAL,
                "General",
                "General notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String,
        channelName: String,
        channelDescription: String,
        importance: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance
            ).apply {
                description = channelDescription
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showOrderStatusNotification(
        context: Context,
        orderId: String,
        title: String,
        message: String,
        largeIcon: Bitmap? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.KEY_ORDER_ID, orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ORDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(orderId.hashCode(), notification)
    }

    fun showPromotionalNotification(
        context: Context,
        title: String,
        message: String,
        imageUrl: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, Constants.CHANNEL_PROMOTIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        if (imageUrl != null) {
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(loadNotificationImage(imageUrl))
            )
        } else {
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
        }

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    fun showGeneralNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun loadNotificationImage(imageUrl: String): Bitmap? {
        return try {
            // Use your image loading library here (e.g., Glide, Picasso)
            // For now, returning null as placeholder
            null
        } catch (e: Exception) {
            null
        }
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun isChannelEnabled(context: Context, channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = notificationManager.getNotificationChannel(channelId)
            return channel?.importance != NotificationManager.IMPORTANCE_NONE
        }
        return true
    }

    fun openNotificationSettings(context: Context) {
        val intent = Intent().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                else -> {
                    action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = android.net.Uri.parse("package:${context.packageName}")
                }
            }
        }
        context.startActivity(intent)
    }
}
