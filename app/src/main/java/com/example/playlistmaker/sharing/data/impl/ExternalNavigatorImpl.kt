package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {
    override fun shareLink(appLink: String) {
        Intent().addFlags(FLAG_ACTIVITY_NEW_TASK).apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, appLink)
            type = "text/plain"
            context.startActivity(this, null)
        }
    }

    override fun openLink(termsLink: String) {
        val browseIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(termsLink)
        ).addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browseIntent)
    }

    override fun openEmail(email: EmailData) {
        Intent().addFlags(FLAG_ACTIVITY_NEW_TASK).apply {
            Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subject_message))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_message))
            context.startActivity(this)
        }
    }
}