package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>()
    val getState: LiveData<PlayerState> = playerState


    fun preparePlaying(onCompletion: () -> Unit) {
        playerInteractor.preparePlaying(
            track,

            object : PlayerRepositoryImpl.OnPreparedListener {
                override fun setOnPreparedListener() {
                    playerState.postValue(PlayerState.STATE_PREPARED)
                }
            },

            object : PlayerRepositoryImpl.OnCompletionListener {
                override fun setOnCompletionListener() {
                    playerState.postValue(PlayerState.STATE_PREPARED)
                    onCompletion()
                }
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlaying()
        playerState.postValue(PlayerState.STATE_PLAYING)
    }

    fun pausePlayer() {
        playerInteractor.pausePlaying()
        playerState.postValue(PlayerState.STATE_PAUSED)
    }

    fun releasePlaying() {
        playerInteractor.releasePlaying()
    }

    fun getCurrentPosition(): Long {
        return playerInteractor.getCurrentPositionPlaying()
    }
}