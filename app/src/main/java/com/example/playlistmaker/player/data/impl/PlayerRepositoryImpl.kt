package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun setDataSource(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
    }

    override fun preparePlaying(onPrepared: () -> Unit) {
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared.invoke()
        }
    }

    override fun onCompletionPlaying(onCompletion: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPositionPlayer(): Int {
        return mediaPlayer.currentPosition
    }
}