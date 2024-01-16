package com.example.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val defaultTimeText = "00:00"

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
    private lateinit var ivPlayButton: ImageView
    private lateinit var ivPauseButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()

        backButtonPlayerActivity.setOnClickListener { finish() }

        val track = getTrack()
        initTrackInfo(track)

        preparePlaying(track)

        ivPlayButton.setOnClickListener { playbackControl() }
        ivPauseButton.setOnClickListener { playbackControl() }
    }

    private fun createUpdateTimer(): Runnable{
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    tvTrackTimePlay.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition + REFRESH_TRACK_TIMER)

                    mainThreadHandler.postDelayed(this, REFRESH_TRACK_TIMER)
                }
            }
        }
    }

    private fun removeUpdateTimer() {
        mainThreadHandler.removeCallbacks(createUpdateTimer())
    }

    private fun startUpdateTimer() {
        mainThreadHandler.post(createUpdateTimer())
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                removeUpdateTimer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                startUpdateTimer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        ivPlayButton.visibility = View.INVISIBLE
        ivPauseButton.visibility = View.VISIBLE
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        ivPlayButton.visibility = View.VISIBLE
        ivPauseButton.visibility = View.INVISIBLE
        playerState = STATE_PAUSED
    }

    private fun preparePlaying(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            ivPlayButton.visibility = View.VISIBLE
            ivPauseButton.visibility = View.INVISIBLE
            playerState = STATE_PREPARED
            removeUpdateTimer()
            tvTrackTimePlay.text = defaultTimeText
        }
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
        ivPlayButton = findViewById(R.id.ivPlayButton)
        ivPauseButton = findViewById(R.id.ivPauseButton)
    }

    private fun getTrack(): Track {
        val json = intent.getStringExtra(TRACK)
        return Gson().fromJson(json, Track::class.java)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        removeUpdateTimer()
    }

    companion object {
        const val TRACK = "TRACK"
        private const val REFRESH_TRACK_TIMER = 500L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}