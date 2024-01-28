package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto

interface MemoryClient {

    fun addSearchHistory(trackArray: List<TrackDto>)

    fun getSearchHistory(): List<TrackDto>

    fun clearSearchHistory()
}