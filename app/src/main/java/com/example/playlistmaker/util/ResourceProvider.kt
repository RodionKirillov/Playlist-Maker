package com.example.playlistmaker.util

import android.app.Application
import android.content.Context

object ResourceProvider {

    lateinit var application: Application

    fun initResourceProvider(application: Application) {
        ResourceProvider.application = application
    }

    fun getString(id: Int): String {
        return application.getString(id)
    }

}