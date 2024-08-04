package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {

    override fun setDataSource(trackUrl: String) {
        playerRepository.setDataSource(trackUrl)
    }

    override fun preparePlaying(onPrepared: () -> Unit) {
        playerRepository.preparePlaying(onPrepared)
    }

    override fun onCompletionPlaying(onCompletion: () -> Unit) {
        playerRepository.onCompletionPlaying(onCompletion)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun getCurrentPositionPlayer(): Int {
        return playerRepository.getCurrentPositionPlayer()
    }
}