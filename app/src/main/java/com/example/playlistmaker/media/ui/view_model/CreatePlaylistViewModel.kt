package com.example.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun insertPlaylist(
        name: String,
        description: String?,
        image: String?,
    ) {
        val playlist = Playlist(
            name = name,
            description = description,
            image = image,
        )
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(playlist)
        }
    }
}