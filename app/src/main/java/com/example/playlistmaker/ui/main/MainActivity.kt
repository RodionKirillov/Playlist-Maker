package com.example.playlistmaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.playlistmaker.ui.media.ActivityMedia
import com.example.playlistmaker.ui.search.ActivitySearch
import com.example.playlistmaker.ui.settings.ActivitySettings
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val searchIntent = Intent(this@MainActivity, ActivitySearch::class.java)
                startActivity(searchIntent)
            }
        }
        searchButton.setOnClickListener(buttonClickListener)

        mediaButton.setOnClickListener {
            val mediaIntent = Intent(this, ActivityMedia::class.java)
            startActivity(mediaIntent)
        }

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, ActivitySettings::class.java)
            startActivity(settingsIntent)
        }
    }
}