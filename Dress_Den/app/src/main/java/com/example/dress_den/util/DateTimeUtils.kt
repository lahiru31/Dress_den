package com.example.dress_den.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val apiDateFormat = SimpleDateFormat(Constants.DateFormats.API_DATE_FORMAT, Locale.US)
    private val displayDateFormat = SimpleDateFormat(Constants.DateFormats.DISPLAY_DATE_FORMAT, Locale.US)
    private val displayTimeFormat = SimpleDateFormat(Constants.DateFormats.DISPLAY_TIME_FORMAT, Locale.US)
    private val displayDateTimeFormat = SimpleDateFormat(Constants.DateFormats.DISPLAY_DATE_TIME_FORMAT, Locale.US)

    fun formatApiDate(date: Date): String {
        return apiDateFormat.format(date)
    }

    fun parseApiDate(dateString: String): Date? {
        return try {
            apiDateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDisplayDate(date: Date): String {
        return displayDateFormat.format(date)
    }

    fun formatDisplayTime(date: Date): String {
        return displayTimeFormat.format(date)
    }

    fun formatDisplayDateTime(date: Date): String {
        return displayDateTimeFormat.format(date)
    }

    fun getRelativeTimeSpan(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            days < 7 -> "$days days ago"
            days < 30 -> "${days / 7} weeks ago"
            days < 365 -> "${days / 30} months ago"
            else -> "${days / 365} years ago"
        }
    }

    fun getDeliveryTimeEstimate(orderDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = orderDate
        calendar.add(Calendar.DAY_OF_MONTH, 3) // Standard delivery time
        return calendar.time
    }

    fun isToday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date
        val calendar2 = Calendar.getInstance()
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date
        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.DAY_OF_YEAR, -1)
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1
        val calendar2 = Calendar.getInstance()
        calendar2.time = date2
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    fun getStartOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return getStartOfDay(calendar.time)
    }

    fun getEndOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
        return getEndOfDay(calendar.time)
    }

    fun getStartOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(calendar.time)
    }

    fun getEndOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return getEndOfDay(calendar.time)
    }

    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.time
    }

    fun getDaysBetween(startDate: Date, endDate: Date): Int {
        val diff = endDate.time - startDate.time
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun getWeeksBetween(startDate: Date, endDate: Date): Int {
        return getDaysBetween(startDate, endDate) / 7
    }

    fun getMonthsBetween(startDate: Date, endDate: Date): Int {
        val calendar1 = Calendar.getInstance()
        calendar1.time = startDate
        val calendar2 = Calendar.getInstance()
        calendar2.time = endDate
        val yearDiff = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR)
        return yearDiff * 12 + calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH)
    }

    fun isDateInRange(date: Date, startDate: Date, endDate: Date): Boolean {
        return !date.before(startDate) && !date.after(endDate)
    }

    fun getTimeAgo(date: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - date.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - date.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - date.time)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - date.time)

        return when {
            seconds < 60 -> "moments ago"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days < 7 -> "$days days ago"
            days < 30 -> "${days / 7} weeks ago"
            days < 365 -> "${days / 30} months ago"
            else -> "${days / 365} years ago"
        }
    }
}
