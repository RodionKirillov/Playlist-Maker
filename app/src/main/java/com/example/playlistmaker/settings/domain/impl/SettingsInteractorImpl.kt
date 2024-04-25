package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.data.model.ThemeSettings
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository):
    SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
       return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }
}