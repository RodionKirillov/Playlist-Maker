package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(appLink: String)
    fun openLink(termsLink: String)
    fun openEmail(email: EmailData)

    fun sharePlaylist(playlistName: String, playlistDescription: String, trackList: List<Track>)
}