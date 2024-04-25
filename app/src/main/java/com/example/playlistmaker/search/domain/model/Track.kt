package com.example.playlistmaker.search.domain.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Track(
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String,
    val trackId: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String? = "Неизвестен"
)
