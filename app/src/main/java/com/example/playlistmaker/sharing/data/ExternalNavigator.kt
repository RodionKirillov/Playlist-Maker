package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(appLink: String)
    fun openLink(termsLink: String)
    fun openEmail(email: EmailData)
}