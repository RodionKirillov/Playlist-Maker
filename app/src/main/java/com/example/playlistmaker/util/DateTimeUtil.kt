package com.example.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtil {
    fun timeFormat(track: Int): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(
            track
        )
    }

    fun dateFormat(date: String): String {
        return if (date != null) {
            val result = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)

            SimpleDateFormat("yyyy", Locale.getDefault()).format(result)
        } else {
            "Неизвестен"
        }
    }
}