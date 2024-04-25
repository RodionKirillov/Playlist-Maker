package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.search.domain.model.Track

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