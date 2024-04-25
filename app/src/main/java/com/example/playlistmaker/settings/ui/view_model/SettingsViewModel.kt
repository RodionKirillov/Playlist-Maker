package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.data.model.ThemeSettings
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.util.App
import com.example.playlistmaker.util.ResourceProvider

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val themeSettings = MutableLiveData<ThemeSettings>()
    val themeState: LiveData<ThemeSettings> = themeSettings

    init {
        themeSettings.postValue(getTheme())
    }

    private fun getTheme(): ThemeSettings {
        return settingsInteractor.getThemeSettings()
    }

    fun setTheme(darkTheme: Boolean) {
        val darkThemeSettings = ThemeSettings(darkTheme)
        themeSettings.postValue(darkThemeSettings)
        settingsInteractor.updateThemeSetting(darkThemeSettings)
        (ResourceProvider.application as App).switchTheme(darkTheme)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }
}