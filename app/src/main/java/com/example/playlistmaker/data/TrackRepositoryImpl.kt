package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track



class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val memoryClient: MemoryClient
) : TrackRepository {

    override fun searchTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(TrackSearchRequest(expression)) // тут ошибка
        return when {
            (response.resultCode == 200) -> {
                val results = (response as TrackResponse).results
                createTrackFromTrackDto(results)

            }

            (response.resultCode == 0) -> {
                null
            }

            else -> {
                emptyList()
            }
        }
    }

    override fun getTracks(): List<Track> {
        val searchHistory = memoryClient.getSearchHistory()
        return createTrackFromTrackDto(searchHistory)
    }

    override fun saveTrack(track: Track) {
        val historyTack = getTracks().toMutableList()
        historyTack.removeAll { it.trackId == track.trackId }
        historyTack.add(0, track)

        if (historyTack.size > maxHistory) historyTack.removeAt(historyTack.size - 1)

        memoryClient.addSearchHistory(createTrackDtoFromTrack(historyTack))
    }

    override fun clearTracks() {
        memoryClient.clearSearchHistory()
    }

    private fun createTrackFromTrackDto(tracks: List<TrackDto>): List<Track> {
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

    private fun createTrackDtoFromTrack(tracks: List<Track>): List<TrackDto> {
        return tracks.map {
            TrackDto(
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

    companion object {
        const val maxHistory = 10
    }
}