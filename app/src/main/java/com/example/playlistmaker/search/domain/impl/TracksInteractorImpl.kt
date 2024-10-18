package com.example.playlistmaker.search.domain.impl


import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.search.domain.interactor.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {

                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun getTracks(): List<Track> {
        return repository.getTracksHistory()
    }

    override fun saveTrack(track: Track) {
        repository.saveTrackToHistory(track)
    }

    override fun clearTracks() {
        repository.clearTrackHistory()
    }
}