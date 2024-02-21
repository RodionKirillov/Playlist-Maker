package com.example.playlistmaker.creator

import com.example.playlistmaker.player.data.PlayerRepository
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl


object Creator {
//    private fun getTracksRepository(): TrackRepository {
//        return TrackRepositoryImpl(RetrofitNetworkClient(), SharedPreferencesMemoryClient())
//    }

//    private fun getPlayerRepository(): PlayerRepository {
//        return PlayerRepositoryImpl()
//    }

//    private fun getExternalNavigator(): ExternalNavigator {
//        return ExternalNavigatorImpl()
//    }

//    private fun getSettingsRepository(): SettingsRepository {
//        return SettingsRepositoryImpl(SharedPreferencesThemeMemory())
//    }

//    fun provideTracksInteractor(): TracksInteractor {
//        return TracksInteractorImpl(getTracksRepository())
//    }

//    fun providePlayerInteractor(): PlayerInteractor {
//        return PlayerInteractorImpl(getPlayerRepository())
//    }

//    fun provideSharingInteractor(): SharingInteractor {
//        return SharingInteractorImpl(getExternalNavigator())
//    }

//    fun provideSettingsInteractor(): SettingsInteractor {
//        return SettingsInteractorImpl(getSettingsRepository())
//    }
}