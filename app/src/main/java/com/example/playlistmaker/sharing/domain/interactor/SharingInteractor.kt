package com.example.playlistmaker.sharing.domain.interactor

import com.example.playlistmaker.search.domain.model.Track

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()

    fun sharePlaylist(playlistName: String, playlistDescription: String, trackList: List<Track>)
}