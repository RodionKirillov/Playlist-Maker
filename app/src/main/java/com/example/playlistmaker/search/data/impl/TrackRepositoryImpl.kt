package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.mappers.SearchMappers
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
    private val mapper: SearchMappers
) : TrackRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {

            -1 -> {
                emit(Resource.Error(INTERNET_ERROR))
            }


            200 -> {
                emit(
                    Resource.Success(
                        mapper.createTrackFromTrackDto((response as TrackResponse).results)
                    )
                )
            }

            else -> {
                emit(Resource.Error(SERVER_ERROR))
            }
        }
    }

    override fun getTracksHistory(): List<Track> {
        return mapper.createTrackFromTrackDto(memoryClient.getSearchHistory())
    }

    override fun saveTrackToHistory(track: Track) {
        memoryClient.addSearchHistory(mapper.createTrackDtoFromTrack(track))
    }

    override fun clearTrackHistory() {
        memoryClient.clearSearchHistory()
    }

    companion object {
        private const val INTERNET_ERROR = "Проверьте подключение к интернету"
        private const val SERVER_ERROR = "Ошибка сервера"
    }
}