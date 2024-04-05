package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun getTracks(): List<Track>

    fun saveTrack(track: Track)

    fun clearTracks()
}