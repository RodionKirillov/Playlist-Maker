package com.example.playlistmaker

import android.content.Intent
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

class ActivitySettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val image = findViewById<Toolbar>(R.id.back_button)
        val shareButton = findViewById<FrameLayout>(R.id.share_button)
        val supportButton = findViewById<FrameLayout>(R.id.support_button)
        val arrowButton = findViewById<FrameLayout>(R.id.arrow_button)

        image.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.practicum_yandex_android_developer))
            )
            startActivity(shareIntent)
        }


        supportButton.setOnClickListener {
            val message = getString(R.string.support_message)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
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