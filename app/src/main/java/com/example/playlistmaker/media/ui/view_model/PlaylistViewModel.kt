package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val playlistInfo = MutableLiveData<Playlist>()
    private val trackList = MutableLiveData<List<Track>>()

    val getPlaylistInfo: LiveData<Playlist> = playlistInfo
    val getTrackList: LiveData<List<Track>> = trackList

    fun updatePlaylistInfo(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylist(playlistId).collect { playlist ->
                playlistInfo.postValue(playlist)
                if (playlist.trackList.isNotEmpty()) {
                    trackList.postValue(createTracksFromJson(playlist.trackList))
                } else {
                    trackList.postValue(mutableListOf())
                }
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track, trackList: List<Track>) {
        val list = trackList.toMutableList()
        list.remove(track)
        viewModelScope.launch {
            playlistInteractor.updateTrackList(
                playlistId,
                createJsonFromTrack(list),
                list.size
            )
            updatePlaylistInfo(playlistId)
        }
    }

    fun sharingPlaylist() {
        sharingInteractor.sharePlaylist(
            playlistInfo.value!!.name,
            playlistInfo.value!!.description.toString(),
            trackList.value!!
        )
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistInfo.value!!)
        }
    }

    private fun createTracksFromJson(json: String): MutableList<Track> {
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun createJsonFromTrack(tracksList: List<Track>): String {
        return Gson().toJson(tracksList)
    }
}