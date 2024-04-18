package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.data.FavoriteRepository
import com.example.playlistmaker.media.domain.FavoriteInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository
) : FavoriteInteractor {
    override suspend fun insertTrackToFavorite(track: Track) {
        favoriteRepository.insertTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        favoriteRepository.deleteTrackFromFavorite(track)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.getFavoriteTracks()
    }

    override suspend fun getFavoriteTracksId(trackId: String): Flow<List<String>> {
        return favoriteRepository.getFavoriteTracksId(trackId)
    }
}