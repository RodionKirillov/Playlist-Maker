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

    fun dateFormat(date: Date): String {
        return SimpleDateFormat(
            "yyyy",
            Locale.getDefault()
        ).format(date)
    }
}