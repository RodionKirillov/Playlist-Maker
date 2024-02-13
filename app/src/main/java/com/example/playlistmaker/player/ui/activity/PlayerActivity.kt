package com.example.playlistmaker.player.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.activity.dpToPx
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val defaultTimeText = "00:00"

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var viewModel: PlayerViewModel
    private lateinit var playerState: PlayerState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonPlayerActivity.setOnClickListener { finish() }

        val track = getTrack()
        initTrackInfo(track)
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.factory(track)
        )[PlayerViewModel::class.java]

        viewModel.preparePlaying(::onCompletion)
        viewModel.getState.observe(this) { state ->
            playerState = state
        }
        binding.ivPlayButton.setOnClickListener { playbackControl(playerState) }
        binding.ivPauseButton.setOnClickListener { playbackControl(playerState) }
    }

    private fun createUpdateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    binding.tvTrackTimePlay.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(viewModel.getCurrentPosition() + REFRESH_TRACK_TIMER)

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

    private fun playbackControl(state: PlayerState) {
        when (state) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
                removeUpdateTimer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
                startUpdateTimer()
            }
        }
    }

    private fun startPlayer() {
        viewModel.startPlayer()
        binding.ivPlayButton.visibility = View.INVISIBLE
        binding.ivPauseButton.visibility = View.VISIBLE
    }

    private fun pausePlayer() {
        viewModel.pausePlayer()
        binding.ivPlayButton.visibility = View.VISIBLE
        binding.ivPauseButton.visibility = View.INVISIBLE
    }

    private fun onCompletion() {                            // Проверить этот метод
        binding.ivPlayButton.visibility = View.VISIBLE
        binding.ivPauseButton.visibility = View.INVISIBLE
        removeUpdateTimer()
        binding.tvTrackTimePlay.text = defaultTimeText
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
            .into(binding.trackImage)

        binding.tvTrackName.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.tvTrackTime.text = timeFormat(track.trackTime.toInt())
        binding.tvAlbumName.text = track.collectionName
        binding.tvYearName.text = dateFormat(track.releaseDate)
        binding.tvGenreName.text = track.primaryGenreName
        binding.tvCountryName.text = track.country
    }

    private fun timeFormat(track: Int): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(
            track
        )
    }

    private fun dateFormat(date: Date): String {
        return SimpleDateFormat(
            "yyyy",
            Locale.getDefault()
        ).format(date)
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
        viewModel.releasePlaying()
        removeUpdateTimer()
    }

    companion object {
        const val TRACK = "TRACK"
        private const val REFRESH_TRACK_TIMER = 500L
    }
}