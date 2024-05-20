package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylist(playlistId: Long): Flow<Playlist>

    suspend fun updateTrackList(playlistId: Long, trackList: String, trackCount: Int)

    suspend fun updatePlaylist(
        playlistId: Long,
        imageUriName: String?,
        playlistName: String,
        playlistDescription: String?
    )
}