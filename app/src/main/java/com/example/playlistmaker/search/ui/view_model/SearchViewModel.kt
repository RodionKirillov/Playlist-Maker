package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchState

class SearchViewModel(
    private val searchInteractor: TracksInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var latestSearchText: String? = null

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val clickDebounceLiveData = MutableLiveData<Boolean>()
    fun observeClickDebounce(): LiveData<Boolean> = clickDebounceLiveData

    fun showHistory() {
        renderState(SearchState.ShowHistory)
    }

    fun saveTrack(track: Track) {
        searchInteractor.saveTrack(track)
    }

    fun clearTracks() {
        searchInteractor.clearTracks()
    }

    fun getHistoryTracks(): List<Track> {
        return searchInteractor.getTracks()
    }

    fun clickDebounceCheck() {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        clickDebounceLiveData.postValue(current)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        handler.postDelayed(searchRunnable, SEARCH_REQUEST_TOKEN, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            searchInteractor.searchTracks(newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        val tracks = mutableListOf<Track>()

                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(SearchState.Error)
                            }

                            tracks.isEmpty() -> {
                                renderState(SearchState.Empty)
                            }

                            else -> {
                                renderState(SearchState.Content(tracks = tracks))
                            }
                        }
                    }
                })
        }
    }

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}