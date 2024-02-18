package com.example.playlistmaker.search.domain.impl


import com.example.playlistmaker.search.data.TrackRepository
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {

                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }

    override fun getTracks(): List<Track> {
        return repository.getTracks()
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun clearTracks() {
        repository.clearTracks()
    }
}