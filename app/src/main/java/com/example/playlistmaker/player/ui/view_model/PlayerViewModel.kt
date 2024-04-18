package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoriteInteractor
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.STATE_DEFAULT)
    private val isFavoriteTrack = MutableLiveData<Boolean>()

    val getState: LiveData<PlayerState> = playerState
    val isFavorite: LiveData<Boolean> = isFavoriteTrack

    init {
        favoriteTrack(track.trackId)
    }

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

    private fun favoriteTrack(id: String) {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTracksId(id).collect { favorites ->
                if (favorites.isNotEmpty()) {
                    isFavoriteTrack.postValue(true)
                } else {
                    isFavoriteTrack.postValue(false)
                }
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (isFavoriteTrack.value == false) {
                favoriteInteractor.insertTrackToFavorite(track)
                isFavoriteTrack.postValue(true)
            } else {
                favoriteInteractor.deleteTrackFromFavorite(track)
                isFavoriteTrack.postValue(false)
            }
        }
    }
}