package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.impl.FavoriteRepositoryImpl
import com.example.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.media.domain.db.FavoriteRepository
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.impl.FavoriteInteractorImpl
import com.example.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.EditPlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavoriteTracksViewModel(
            favoriteInteractor = get()
        )
    }

    viewModel {
        PlaylistsViewModel(
            playlistInteractor = get()
        )
    }

    viewModel {
        CreatePlaylistViewModel(
            playlistInteractor = get()
        )
    }

    viewModel { (playlistId: Long) ->
        PlaylistViewModel(
            playlistId = playlistId,
            playlistInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel { (playlistId: Long) ->
        EditPlaylistViewModel(
            playlistId = playlistId,
            playlistInteractor = get()
        )
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(
            favoriteRepository = get()
        )
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(
            playlistRepository = get()
        )
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(
            appDatabase = get(),
            trackDbConvertor = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            appDatabase = get(),
            playlistDbConvertor = get()
        )
    }

    factory { PlaylistDbConvertor() }

    factory { TrackDbConvertor() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}