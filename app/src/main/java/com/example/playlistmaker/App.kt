package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val isDarkThemeEnabled =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES


        val sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(
            KEY_NIGHT_THEME,
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

        val sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(KEY_NIGHT_THEME, darkTheme)
            .apply()
    }

    companion object {
        const val SHARED_PREFS = "SHARED_PREFS"
        const val KEY_NIGHT_THEME = "KEY_NIGHT_THEME"
    }
}