package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun getPlaylistTracks(playlistId: Long): Flow<Playlist> {
        return playlistRepository.getPlaylistTracks(playlistId)
    }

    override suspend fun updateTrackList(playlistId: Long, trackList: String, trackCount: Int) {
        playlistRepository.updateTrackList(playlistId, trackList, trackCount)
    }
}