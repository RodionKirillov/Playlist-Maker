package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.search.data.MemoryClient
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TrackRepository
import com.example.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.search.data.memory.SharedPreferencesMemoryClient
import com.example.playlistmaker.search.data.network.ITunesSearchAPI
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    viewModel {
        SearchViewModel(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<ITunesSearchAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchAPI::class.java)
    }

    single<MemoryClient> {
        SharedPreferencesMemoryClient(get())
    }

    single {
        androidContext().getSharedPreferences(
            SharedPreferencesMemoryClient.SHARED_PREFS_HISTORY,
            Context.MODE_PRIVATE
        )
    }
}