package com.example.playlistmaker.settings.domain.repository

import com.example.playlistmaker.settings.data.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}