package com.example.playlistmaker.player.domain.repository

interface PlayerRepository {

    fun setDataSource(trackUrl: String)

    fun preparePlaying(onPrepared: () -> Unit)

    fun onCompletionPlaying(onCompletion: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getCurrentPositionPlayer(): Int
}