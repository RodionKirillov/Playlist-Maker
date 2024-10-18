package com.example.playlistmaker.search.data.source

import com.example.playlistmaker.search.data.dto.TrackDto

interface MemoryClient {

    fun addSearchHistory(track: TrackDto)

    fun getSearchHistory(): List<TrackDto>

    fun clearSearchHistory()
}