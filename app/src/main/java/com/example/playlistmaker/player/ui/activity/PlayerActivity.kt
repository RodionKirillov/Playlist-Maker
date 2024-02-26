package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.activity.dpToPx
import com.example.playlistmaker.util.DateTimeUtil
import com.example.playlistmaker.util.ResourceProvider
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playerState: PlayerState
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel() {
        parametersOf(track)
    }
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val defaultTimeText = ResourceProvider.getString(R.string.default_track_time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonPlayerActivity.setOnClickListener { finish() }

        track = getTrack()
        initTrackInfo(track)

        viewModel.preparePlaying(::onCompletion)
        viewModel.getState.observe(this) { state ->
            playerState = state
        }
        binding.ivPlayButton.setOnClickListener {
            if (playerState == PlayerState.STATE_PREPARED || playerState == PlayerState.STATE_PAUSED) {
                playbackControl(playerState)
            }
        }
        binding.ivPauseButton.setOnClickListener { playbackControl(playerState) }
    }

    private fun createUpdateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    binding.tvTrackTimePlay.text =
                        DateTimeUtil.timeFormat(
                            (viewModel.getCurrentPosition() + REFRESH_TRACK_TIMER_MILLIS).toInt()
                        )

                    mainThreadHandler.postDelayed(this, REFRESH_TRACK_TIMER_MILLIS)
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

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED, PlayerState.STATE_DEFAULT -> {
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

    private fun onCompletion() {
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
        binding.tvTrackTime.text = DateTimeUtil.timeFormat(track.trackTime.toInt())
        binding.tvAlbumName.text = track.collectionName
        binding.tvYearName.text = DateTimeUtil.dateFormat(track.releaseDate)
        binding.tvGenreName.text = track.primaryGenreName
        binding.tvCountryName.text = track.country
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
        private const val REFRESH_TRACK_TIMER_MILLIS = 500L
    }
}