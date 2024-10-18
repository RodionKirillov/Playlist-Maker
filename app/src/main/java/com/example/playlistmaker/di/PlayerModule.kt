package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get(),
            favoriteInteractor = get(),
            playlistInteractor = get()
        )
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(
            playerRepository = get()
        )
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(
            mediaPlayer = get()
        )
    }

    factory<MediaPlayer> { MediaPlayer() }
}