package com.example.playlistmaker.search.data.dto

data class TrackResponse(
    val resultCount: Int,
    val results: ArrayList<TrackDto>
): Response()
