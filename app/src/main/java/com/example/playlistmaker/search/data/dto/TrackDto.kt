package com.example.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TrackDto(
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String,
    val trackId: String,
    val collectionName: String,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)