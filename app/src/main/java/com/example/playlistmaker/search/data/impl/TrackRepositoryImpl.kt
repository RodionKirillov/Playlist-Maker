package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.MemoryClient
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.TrackRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource


class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val memoryClient: MemoryClient
) : TrackRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {

            0 -> {
                Resource.Error("Проверьте подключение к интернету")
            }


            200 -> {
                Resource.Success(createTrackFromTrackDto((response as TrackResponse).results))
            }

            else -> {
                Resource.Error("Ошибка сервера")
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