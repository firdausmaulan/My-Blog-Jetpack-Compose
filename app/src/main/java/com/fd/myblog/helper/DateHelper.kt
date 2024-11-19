package com.fd.myblog.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateHelper {
    fun formatDate(timestamp: String?): String {
        if (timestamp == null) return "Invalid timestamp"
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = formatter.parse(timestamp) ?: return "Invalid date"
        val outputFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return outputFormatter.format(date)
    }
}