package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.domain.models.Track

interface PlayerInteractor {

    fun preparePlaying(
        track: Track,
        onPreparedListener: PlayerRepositoryImpl.OnPreparedListener,
        onCompletionListener: PlayerRepositoryImpl.OnCompletionListener
    )

    fun startPlaying()

    fun pausePlaying()

    fun releasePlaying()

    fun getCurrentPositionPlaying(): Long
}