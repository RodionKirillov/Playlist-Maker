package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.playlistmaker.App.Companion.KEY_NIGHT_THEME
import com.example.playlistmaker.App.Companion.SHARED_PREFS
import com.google.android.material.switchmaterial.SwitchMaterial

class ActivitySettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Toolbar>(R.id.back_button)
        val shareButton = findViewById<FrameLayout>(R.id.share_button)
        val supportButton = findViewById<FrameLayout>(R.id.support_button)
        val arrowButton = findViewById<FrameLayout>(R.id.arrow_button)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)


        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> themeSwitcher.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> themeSwitcher.isChecked = false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> themeSwitcher.isChecked = false
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.practicum_yandex_android_developer)
            )
            startActivity(Intent.createChooser(shareIntent, null))
        }

        supportButton.setOnClickListener {
            val message = getString(R.string.support_message)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_message))
            startActivity(shareIntent)
        }

        arrowButton.setOnClickListener {
            val browseIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.yandex_practicum_offer))
            )
            startActivity(browseIntent)
        }
    }
}