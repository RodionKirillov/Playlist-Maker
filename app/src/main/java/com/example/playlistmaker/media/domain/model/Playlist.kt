package com.example.playlistmaker.media.domain.model

data class Playlist(
    val playlistId: Long = 0,
    val name: String,
    val description: String?,
    val image: String?,
    val trackList: String = "",
    val trackCount: Int = 0
)
