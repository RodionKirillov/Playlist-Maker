package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun sharePlaylist(
        playlistName: String,
        playlistDescription: String,
        trackList: List<Track>
    ) {
        externalNavigator.sharePlaylist(playlistName, playlistDescription, trackList)
    }

    private fun getShareAppLink(): String {
        return APP_LINK
    }

    private fun getTermsLink(): String {
        return TERMS_LINK
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(SUPPORT_EMAIL)
    }


    companion object {
        const val APP_LINK = "https://practicum.yandex.ru/android-developer/?from=catalog"
        const val TERMS_LINK = "https://yandex.ru/legal/practicum_offer/"
        const val SUPPORT_EMAIL = "rodion.44454@gmail.com"
    }
}