package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun getTracksHistory(): List<Track>

    fun saveTrackToHistory(track: Track)

    fun clearTrackHistory()
}