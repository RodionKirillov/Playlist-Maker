package com.example.playlistmaker.settings.data.memory

import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.ThemeMemory
import com.example.playlistmaker.settings.data.model.ThemeSettings

class SharedPreferencesThemeMemory(private val sharedPrefs: SharedPreferences) : ThemeMemory {

    override fun getThemeSettings(): ThemeSettings {
        val isDarkTheme = sharedPrefs.getBoolean(SHARED_PREFS_THEME_KEY, false)
        return ThemeSettings(isDarkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(
            SHARED_PREFS_THEME_KEY,
            settings.darkTheme
        ).apply()
    }

    companion object {
        const val SHARED_PREFS_THEME = "SHARED_PREFS_THEME"
        const val SHARED_PREFS_THEME_KEY = "SHARED_PREFS_THEME_KEY"
    }
}