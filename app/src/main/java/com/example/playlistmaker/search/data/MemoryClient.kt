package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackDto

interface MemoryClient {

    fun addSearchHistory(trackArray: List<TrackDto>)

    fun getSearchHistory(): List<TrackDto>

    fun clearSearchHistory()
}