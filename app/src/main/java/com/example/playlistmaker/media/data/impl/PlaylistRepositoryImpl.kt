package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlist = appDatabase.playlistDao().getPlaylists().map { playlistEntity ->
            playlistDbConvertor.map(playlistEntity)
        }
        emit(playlist)
    }

    override suspend fun getPlaylistTracks(playlistId: Long): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistTracks(playlistId)
        emit(playlistDbConvertor.map(playlist))
    }

    override suspend fun updateTrackList(playlistId: Long, trackList: String, trackCount: Int) {
        appDatabase.playlistDao().updateTrackList(playlistId, trackList, trackCount)
    }
}