package com.example.playlistmaker.search.ui.model

import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    object Loading : SearchState

    object Error : SearchState

    object Empty : SearchState

    object ShowHistory: SearchState

}