package com.example.playlistmaker.player.domain.interactor

interface PlayerInteractor {

    fun setDataSource(trackUrl: String)

    fun preparePlaying(onPrepared: () -> Unit)

    fun onCompletionPlaying(onCompletion: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getCurrentPositionPlayer(): Int
}