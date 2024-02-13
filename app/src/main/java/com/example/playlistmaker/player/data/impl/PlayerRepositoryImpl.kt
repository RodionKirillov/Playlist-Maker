package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepository
import com.example.playlistmaker.search.domain.model.Track

class PlayerRepositoryImpl() : PlayerRepository {

    private var mediaPlayer = MediaPlayer()

    override fun preparePlaying(
        track: Track,
        onPreparedListener: OnPreparedListener,
        onCompletionListener: OnCompletionListener
    ) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPreparedListener.setOnPreparedListener()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletionListener.setOnCompletionListener()
        }
    }

    override fun startPlaying() {
        mediaPlayer.start()
    }

    override fun pausePlaying() {
        mediaPlayer.pause()
    }

    override fun releasePlaying() {
        mediaPlayer.release()
    }

    override fun getCurrentPositionPlaying(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    interface OnPreparedListener {
        fun setOnPreparedListener()
    }

    interface OnCompletionListener {
        fun setOnCompletionListener()
    }
}