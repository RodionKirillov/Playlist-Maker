package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.converters.SearchDtoConverter
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.source.MemoryClient
import com.example.playlistmaker.search.data.source.NetworkClient
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val memoryClient: MemoryClient,
    private val converter: SearchDtoConverter
) : TrackRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {

            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                emit(
                    Resource.Success(
                        converter.createTrackFromTrackDto((response as TrackResponse).results)
                    )
                )
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    override fun getTracks(): List<Track> {
        val searchHistory = memoryClient.getSearchHistory()
        return converter.createTrackFromTrackDto(searchHistory)
    }

    override fun saveTrack(track: Track) {
        val historyTack = getTracks().toMutableList()
        historyTack.removeAll { it.trackId == track.trackId }
        historyTack.add(0, track)

        if (historyTack.size > maxHistory) historyTack.removeAt(historyTack.size - 1)

        memoryClient.addSearchHistory(converter.createTrackDtoFromTrack(historyTack))
    }

    override fun clearTracks() {
        memoryClient.clearSearchHistory()
    }

    companion object {
        private const val maxHistory = 10
    }
}