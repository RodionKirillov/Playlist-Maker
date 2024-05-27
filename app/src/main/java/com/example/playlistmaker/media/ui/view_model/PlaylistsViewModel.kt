package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.ui.model.PlaylistState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistState>()

    val getState: LiveData<PlaylistState> = playlistState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    playlistState.postValue(PlaylistState.Empty)
                } else {
                    playlistState.postValue(PlaylistState.Content(playlists.reversed()))
                }
            }
        }
    }
}