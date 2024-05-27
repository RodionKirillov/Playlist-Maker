package com.example.playlistmaker.sharing.data.impl

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.util.ResourceProvider

class ExternalNavigatorImpl : ExternalNavigator {
    override fun shareLink(appLink: String) {
        Intent().addFlags(FLAG_ACTIVITY_NEW_TASK).apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, appLink)
            type = "text/plain"
            ResourceProvider.application.startActivity(this, null)
        }
    }

    override fun openLink(termsLink: String) {
        val browseIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(termsLink)
        ).addFlags(FLAG_ACTIVITY_NEW_TASK)
        ResourceProvider.application.startActivity(browseIntent)
    }

    override fun openEmail(email: EmailData) {
        Intent().addFlags(FLAG_ACTIVITY_NEW_TASK).apply {
            Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(
                Intent.EXTRA_SUBJECT,
                ResourceProvider.application.getString(R.string.subject_message)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                ResourceProvider.application.getString(R.string.support_message)
            )
            ResourceProvider.application.startActivity(this)
        }
    }

    override fun sharePlaylist(
        playlistName: String,
        playlistDescription: String,
        trackList: List<Track>
    ) {
        Intent().addFlags(FLAG_ACTIVITY_NEW_TASK).apply {
            action = Intent.ACTION_SEND

            val messageBuilder = StringBuilder()
            messageBuilder.append(playlistName).append("\n")
            if (playlistDescription.isNotEmpty()) {
                messageBuilder.append(playlistDescription).append("\n\n")
            }
            messageBuilder.append("${trackList.size} треков\n\n")

            trackList.forEachIndexed { index, track ->
                messageBuilder.append("${index + 1}. ${track.artistName} - ${track.trackName}\n")
            }

            putExtra(Intent.EXTRA_TEXT, messageBuilder.toString())
            type = "text/plain"
            ResourceProvider.application.startActivity(this, null)
        }
    }
}