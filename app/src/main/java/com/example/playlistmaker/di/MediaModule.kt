package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavoriteTracksViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}