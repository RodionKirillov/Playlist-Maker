package com.example.playlistmaker.settings.data.memory

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.settings.data.ThemeMemory
import com.example.playlistmaker.settings.data.model.ThemeSettings
import com.example.playlistmaker.util.ResourceProvider

class SharedPreferencesThemeMemory : ThemeMemory {
    private val sharedPrefs =  ResourceProvider.application.getSharedPreferences(SHARED_PREFS_THEME, MODE_PRIVATE)

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