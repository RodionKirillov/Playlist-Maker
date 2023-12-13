package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var backButtonPlayerActivity: android.widget.Toolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackTimeTextView: TextView
    private lateinit var albumTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()

        backButtonPlayerActivity.setOnClickListener { finish() }

        val track = getTrack()
        initTrackInfo(track)

    }
    private fun getCoverArtwork(track: Track){
        track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }

    private fun initTrackInfo(track: Track) {
        Glide.with(this)
            .load(getCoverArtwork(track))
            .placeholder(R.drawable.placeholder_icon)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = timeFormat(track.trackTime.toInt())
        trackTimeTextView.text = timeFormat(track.trackTime.toInt())
        albumTextView.text = track.collectionName
        yearTextView.text = SimpleDateFormat(
            "yyyy",
            Locale.getDefault()
        ).format(track.releaseDate)
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country
    }

    private fun timeFormat(track: Int): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(
            track
        )
    }

    private fun initViews() {
        backButtonPlayerActivity = findViewById(R.id.backButtonPlayerActivity)
        trackImage = findViewById(R.id.trackImage)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        trackTimeTextView = findViewById(R.id.trackTimeTextView)
        albumTextView = findViewById(R.id.albumTextView)
        yearTextView = findViewById(R.id.yearTextView)
        genreTextView = findViewById(R.id.genreTextView)
        countryTextView = findViewById(R.id.countryTextView)
    }

    private fun getTrack(): Track {
        val json = intent.getStringExtra(TRACK)
        return Gson().fromJson(json, Track::class.java)
    }

    companion object {
        const val TRACK = "TRACK"
    }
}