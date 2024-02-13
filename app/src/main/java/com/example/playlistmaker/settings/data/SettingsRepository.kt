package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.data.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}