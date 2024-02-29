package com.example.playlistmaker.util

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.mediaModule
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.di.sharingModule
import com.example.playlistmaker.settings.data.memory.SharedPreferencesThemeMemory.Companion.SHARED_PREFS_THEME
import com.example.playlistmaker.settings.data.memory.SharedPreferencesThemeMemory.Companion.SHARED_PREFS_THEME_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(settingsModule, sharingModule, searchModule, playerModule, mediaModule)
        }

        ResourceProvider.initResourceProvider(this)

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