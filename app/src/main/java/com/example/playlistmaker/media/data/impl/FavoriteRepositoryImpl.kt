package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.FavoriteRepository
import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteRepository {

    override suspend fun insertTrackToFavorite(track: Track) {
        appDatabase.trackDao().insertTrackToFavorite(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        appDatabase.trackDao().deleteTrackFromFavorite(trackDbConvertor.map(track))
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> = flow{
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(converterFromTrackEntity(tracks))
    }

    override suspend fun getFavoriteTracksId(trackId: String): Flow<List<String>> = flow{
        emit(appDatabase.trackDao().getFavoriteTracksId(trackId))
    }

    private fun converterFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}