package com.example.playlistmaker.search.data.mappers

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.model.Track

class SearchMappers {

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

    fun createTrackDtoFromTrack(track: Track): TrackDto {
        return TrackDto(
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate!!,
            track.primaryGenreName,
            track.country,
            track.previewUrl!!
        )
    }
}