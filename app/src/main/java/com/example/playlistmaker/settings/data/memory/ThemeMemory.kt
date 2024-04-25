package com.example.playlistmaker.settings.data.memory

import com.example.playlistmaker.settings.data.model.ThemeSettings

interface ThemeMemory {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}