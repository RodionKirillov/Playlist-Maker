package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoriteInteractor
import com.example.playlistmaker.media.ui.model.FavoriteState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val favoriteState = MutableLiveData<FavoriteState>()

    val getState: LiveData<FavoriteState> = favoriteState

    fun getFavoritesTrack() {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTracks().collect { tracks ->
                if (tracks.isEmpty()) {
                    favoriteState.postValue(FavoriteState.Empty)
                } else {
                    favoriteState.postValue(FavoriteState.Content(tracks.reversed()))
                }
            }
        }
    }
}