package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.data.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}