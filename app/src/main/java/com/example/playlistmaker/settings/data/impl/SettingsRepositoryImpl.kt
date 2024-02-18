package com.example.playlistmaker.settings.data.impl

import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.ThemeMemory
import com.example.playlistmaker.settings.data.model.ThemeSettings

class SettingsRepositoryImpl(private val themeMemory: ThemeMemory): SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return themeMemory.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        themeMemory.updateThemeSetting(settings)
    }
}