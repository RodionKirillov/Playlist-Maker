package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>?

    fun getTracks(): List<Track>

    fun saveTrack(track: Track)

    fun clearTracks()
}