package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.DateTimeUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    private val isFavoriteTrack = MutableLiveData<Boolean>()
    private val playlistList = MutableLiveData<List<Playlist>>()
    private val containsToPlaylist = MutableLiveData<Pair<String, Boolean>>()

    val playerState: LiveData<PlayerState> = _playerState
    val isFavorite: LiveData<Boolean> = isFavoriteTrack
    val getPlaylistList: LiveData<List<Playlist>> = playlistList
    val getContainsToPlaylist: LiveData<Pair<String, Boolean>> = containsToPlaylist

    private var currentPositionJob: Job? = null

    init {
        initMediaPlayer()
        favoriteTrack(track.trackId)
    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {

            is PlayerState.Playing -> pausePlaying()

            is PlayerState.Prepared, is PlayerState.Paused -> startPlaying()

            else -> {}
        }
    }

    fun onPause() {
        pausePlaying()
    }

    private fun initMediaPlayer() {
        track.previewUrl?.let {
            playerInteractor.setDataSource(track.previewUrl)
            playerInteractor.preparePlaying {
                _playerState.postValue(PlayerState.Prepared)
            }
        }

        playerInteractor.onCompletionPlaying {
            _playerState.postValue(PlayerState.Prepared)
            stopTimer()
        }
    }

    private fun startPlaying() {
        playerInteractor.startPlayer()
        currentPositionJob = viewModelScope.launch {
            while (true) {
                _playerState.postValue(PlayerState.Playing(getCurrentPosition()))
                delay(REFRESH_TRACK_TIMER_MILLIS)
            }
        }
    }

    private fun pausePlaying() {
        playerInteractor.pausePlayer()
        _playerState.postValue(PlayerState.Paused)
        stopTimer()
    }

    private fun releasePlayer() {
        playerInteractor.releasePlayer()
        _playerState.value = PlayerState.Default
        stopTimer()
    }

    private fun getCurrentPosition(): String {
        return DateTimeUtil.timeFormat(playerInteractor.getCurrentPositionPlayer())
    }

    private fun stopTimer() {
        currentPositionJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun getPlayLists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlist ->
                playlistList.postValue(playlist.reversed())
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

    fun getPlaylistTracks(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylist(playlistId).collect { playlist ->
                checkTrackToPlaylist(playlist)
            }
        }
    }

    private fun createJsonFromTrack(tracksList: List<Track>): String {
        return Gson().toJson(tracksList)
    }

    private fun createTracksFromJson(json: String): MutableList<Track> {
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
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

    companion object {

        private const val REFRESH_TRACK_TIMER_MILLIS = 300L
    }
}