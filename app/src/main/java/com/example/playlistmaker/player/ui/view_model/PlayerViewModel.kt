package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.STATE_DEFAULT)
    private val isFavoriteTrack = MutableLiveData<Boolean>()
    private val playlistList = MutableLiveData<List<Playlist>>()
    private val containsToPlaylist = MutableLiveData<Pair<String, Boolean>>()

    val getState: LiveData<PlayerState> = playerState
    val isFavorite: LiveData<Boolean> = isFavoriteTrack
    val getPlaylistList: LiveData<List<Playlist>> = playlistList
    val getContainsToPlaylist: LiveData<Pair<String, Boolean>> = containsToPlaylist

    init {
        favoriteTrack(track.trackId)
    }

    fun getPlaylistTracks(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistTracks(playlistId).collect { playlist ->
                checkTrackToPlaylist(playlist)
            }
        }
    }

    //            Log.e("CreatePlaylistViewModel999", tracksList.toString())
    private fun checkTrackToPlaylist(playlist: Playlist) {
        if (playlist.trackList.isNotEmpty()) {
            val trackList = createTracksFromJson(playlist.trackList)

            if (trackList.contains(track)) {
                containsToPlaylist.postValue(Pair(playlist.name, true))
            } else {
                containsToPlaylist.postValue(Pair(playlist.name, false))
                trackList.add(track)
                addTrackToPlaylist(playlist.playlistId, trackList, trackList.size)
            }
        } else {

            val trackList = mutableListOf<Track>()
            containsToPlaylist.postValue(Pair(playlist.name, false))
            trackList.add(track)
            addTrackToPlaylist(playlist.playlistId, trackList, trackList.size)
        }
    }

    private fun addTrackToPlaylist(playlistId: Long, tracksList: List<Track>, trackCount: Int) {
        viewModelScope.launch {
            playlistInteractor.updateTrackList(
                playlistId,
                createJsonFromTrack(tracksList),
                trackCount
            )
        }
    }

    private fun createJsonFromTrack(tracksList: List<Track>): String {
        return Gson().toJson(tracksList)
    }

    private fun createTracksFromJson(json: String): MutableList<Track> {
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun getPlayLists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlist ->
                playlistList.postValue(playlist.reversed())
            }
        }
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