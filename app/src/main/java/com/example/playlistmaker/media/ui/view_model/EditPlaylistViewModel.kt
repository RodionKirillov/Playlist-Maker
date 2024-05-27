package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlist = MutableLiveData<Playlist>()

    val getPlaylistInfo: LiveData<Playlist> = playlist

    fun getPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylist(playlistId).collect { playlistInfo ->
                playlist.postValue(playlistInfo)
            }
        }
    }

    fun editPlaylist(
        imageUriName: String?,
        playlistName: String,
        playlistDescription: String?
    ) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(
                playlistId,
                imageUriName,
                playlistName,
                playlistDescription
            )
        }
    }
}