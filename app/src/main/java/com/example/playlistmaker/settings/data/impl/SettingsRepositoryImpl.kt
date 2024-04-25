package com.example.playlistmaker.settings.data.impl

import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.data.memory.ThemeMemory
import com.example.playlistmaker.settings.data.model.ThemeSettings

class SettingsRepositoryImpl(private val themeMemory: ThemeMemory): SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return themeMemory.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        themeMemory.updateThemeSetting(settings)
    }
}