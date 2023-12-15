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
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvTrackTimePlay: TextView
    private lateinit var tvTrackTime: TextView
    private lateinit var tvAlbumName: TextView
    private lateinit var tvYearName: TextView
    private lateinit var tvGenreName: TextView
    private lateinit var tvCountryName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()

        backButtonPlayerActivity.setOnClickListener { finish() }

        val track = getTrack()
        initTrackInfo(track)

    }

    private fun getCoverArtwork(track: Track): String {
        return track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun initTrackInfo(track: Track) {
        Glide.with(this)
            .load(getCoverArtwork(track))
            .placeholder(R.drawable.placeholder_icon)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(trackImage)

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvTrackTimePlay.text = timeFormat(track.trackTime.toInt())
        tvTrackTime.text = timeFormat(track.trackTime.toInt())
        tvAlbumName.text = track.collectionName
        tvYearName.text = SimpleDateFormat(
            "yyyy",
            Locale.getDefault()
        ).format(track.releaseDate)
        tvGenreName.text = track.primaryGenreName
        tvCountryName.text = track.country
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
        tvTrackName = findViewById(R.id.tvTrackName)
        tvArtistName = findViewById(R.id.tvArtistName)
        tvTrackTimePlay = findViewById(R.id.tvTrackTimePlay)
        tvTrackTime = findViewById(R.id.tvTrackTime)
        tvAlbumName = findViewById(R.id.tvAlbumName)
        tvYearName = findViewById(R.id.tvYearName)
        tvGenreName = findViewById(R.id.tvGenreName)
        tvCountryName = findViewById(R.id.tvCountryName)
    }

    private fun getTrack(): Track {
        val json = intent.getStringExtra(TRACK)
        return Gson().fromJson(json, Track::class.java)
    }

    companion object {
        const val TRACK = "TRACK"
    }
}