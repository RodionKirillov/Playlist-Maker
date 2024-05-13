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

//    private var playlistName: String = ""
//    private var playlistDescription: String = ""
//    private var imageUri: String = ""
//
//    private fun setPlaylistName(name: String) {
//        playlistName = name
//    }
//
//    private fun setPlaylistDescription(description: String) {
//        playlistDescription = description
//    }
//
//    private fun setImageUri(imageUri: String) {
//        this.imageUri = imageUri
//    }


    fun insertPlaylist(
        name: String,
        description: String,
        image: String,
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