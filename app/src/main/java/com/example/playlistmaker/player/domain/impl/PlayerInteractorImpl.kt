package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.search.domain.model.Track

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {

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