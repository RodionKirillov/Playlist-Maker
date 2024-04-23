package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.media.domain.db.FavoriteRepository
import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.impl.FavoriteRepositoryImpl
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.media.domain.impl.FavoriteInteractorImpl
import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    factory { TrackDbConvertor() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}