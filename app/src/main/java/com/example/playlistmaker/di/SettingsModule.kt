package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.ThemeMemory
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.memory.SharedPreferencesThemeMemory
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val settingsModule = module {

    viewModel {
        SettingsViewModel(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ThemeMemory> {
        SharedPreferencesThemeMemory(get())
    }

    single {
        androidContext().getSharedPreferences(
            SharedPreferencesThemeMemory.SHARED_PREFS_THEME,
            Context.MODE_PRIVATE
        )
    }
}
