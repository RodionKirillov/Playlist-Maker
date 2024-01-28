package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.Track

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun preparePlaying(
        track: Track,
        onPreparedListener: PlayerRepositoryImpl.OnPreparedListener,
        onCompletionListener: PlayerRepositoryImpl.OnCompletionListener
    ) {
        playerRepository.preparePlaying(
            track,
            onPreparedListener,
            onCompletionListener
        )
    }

    override fun startPlaying() {
        playerRepository.startPlaying()
    }

    override fun pausePlaying() {
        playerRepository.pausePlaying()
    }

    override fun releasePlaying() {
        playerRepository.releasePlaying()
    }

    override fun getCurrentPositionPlaying(): Long {
        return playerRepository.getCurrentPositionPlaying()
    }
}