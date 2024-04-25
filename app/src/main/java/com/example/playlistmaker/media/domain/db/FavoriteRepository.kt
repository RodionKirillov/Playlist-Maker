package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun insertTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(track: Track)

    suspend fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun getFavoriteTracksId(trackId: String): Flow<List<String>>
}