package com.example.playlistmaker.util

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.memory.SharedPreferencesThemeMemory.Companion.SHARED_PREFS_THEME
import com.example.playlistmaker.settings.data.memory.SharedPreferencesThemeMemory.Companion.SHARED_PREFS_THEME_KEY

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val isDarkThemeEnabled =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES


        val sharedPrefs = getSharedPreferences(SHARED_PREFS_THEME, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(
            SHARED_PREFS_THEME_KEY,
            isDarkThemeEnabled
        )
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        val sharedPrefs = getSharedPreferences(SHARED_PREFS_THEME, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(SHARED_PREFS_THEME_KEY, darkTheme)
            .apply()
    }
}