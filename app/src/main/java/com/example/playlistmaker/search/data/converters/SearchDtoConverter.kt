package com.example.playlistmaker.search.data.converters

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.model.Track

class SearchDtoConverter {

    fun createTrackFromTrackDto(tracks: List<TrackDto>): List<Track> {
        return tracks.map {
            Track(
                it.trackName,
                it.artistName,
                it.trackTime,
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

    fun createTrackDtoFromTrack(tracks: List<Track>): List<TrackDto> {
        return tracks.map {
            TrackDto(
                it.trackName,
                it.artistName,
                it.trackTime,
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate!!,
                it.primaryGenreName,
                it.country,
                it.previewUrl!!
            )
        }
    }
}