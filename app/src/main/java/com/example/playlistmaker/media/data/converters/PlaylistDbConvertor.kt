package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist

class PlaylistDbConvertor {

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            name = playlistEntity.name,
            description = playlistEntity.description,
            image = playlistEntity.image,
            trackList = playlistEntity.trackList,
            trackCount = playlistEntity.trackCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            name = playlist.name,
            description = playlist.description,
            image = playlist.image,
            trackList = playlist.trackList,
            trackCount = playlist.trackCount
        )
    }
}