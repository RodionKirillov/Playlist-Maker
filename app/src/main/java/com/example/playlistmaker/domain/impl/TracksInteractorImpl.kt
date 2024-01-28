package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TrackRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
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