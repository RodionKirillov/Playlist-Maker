package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val sharingModule = module {

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }
}