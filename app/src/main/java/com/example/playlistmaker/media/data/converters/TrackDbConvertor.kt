package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConvertor {

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTime = trackEntity.trackTime,
            artworkUrl100 = trackEntity.artworkUrl100,
            trackId = trackEntity.trackId,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate!!,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl!!
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate!!,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl!!
        )
    }
}