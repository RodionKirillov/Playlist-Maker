package com.example.playlistmaker.player.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.media.ui.fragment.CreatePlaylistFragment
import com.example.playlistmaker.player.ui.adapter.PlaylistBottomSheetAdapter
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.view_holder.dpToPx
import com.example.playlistmaker.util.DateTimeUtil
import com.example.playlistmaker.util.ResourceProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private var playlistAdapter: PlaylistBottomSheetAdapter? = null
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playerState: PlayerState
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel() {
        parametersOf(track)
    }
    private var timerJob: Job? = null
    private val defaultTimeText = ResourceProvider.getString(R.string.default_track_time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonPlayerActivity.setOnClickListener { finish() }

        track = getTrack()
        initTrackInfo(track)

        setupBottomSheet()

        viewModel.getPlaylistList.observe(this) { playlists ->
            playlistAdapter?.submitList(playlists)
        }

        viewModel.getContainsToPlaylist.observe(this) {
            showToast(it.first, it.second)
        }

        binding.addPlaylistButton.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetPlaylists).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
            if (savedInstanceState == null) {
                supportFragmentManager.commit {
                    add<CreatePlaylistFragment>(R.id.playlistBottomSheetFragmentContainerView)
                }
            }
        }

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

        binding.ivAddToFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) {
                binding.ivAddToFavoriteButton.setImageResource(R.drawable.add_to_favorite_on_icon)
            } else {
                binding.ivAddToFavoriteButton.setImageResource(R.drawable.add_to_favorite_icon)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlayLists()
    }

    private fun setupBottomSheet() {

        setupRecyclerView()

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylists).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.ivAddToPlaylistButton.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetPlaylists).apply {
                viewModel.getPlayLists()
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                binding.overlay.alpha = slideOffset
            }
        })
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistBottomSheetAdapter()
        binding.rvPlaylists.adapter = playlistAdapter

        playlistAdapter?.onPlaylistClickListener = {
            viewModel.getPlaylistTracks(it.playlistId)
            BottomSheetBehavior.from(binding.bottomSheetPlaylists).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun showToast(playlistName: String, isShow: Boolean) {
        if (isShow) {
            Toast.makeText(
                this,
                getString(R.string.toast_track_add_true, playlistName),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.toast_track_add_false, playlistName),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createUpdateTimer() {
        timerJob = lifecycleScope.launch {
            while (true) {
                delay(REFRESH_TRACK_TIMER_MILLIS)
                binding.tvTrackTimePlay.text = DateTimeUtil.timeFormat(
                    viewModel.getCurrentPosition().toInt()
                )
            }
        }
    }

    private fun removeUpdateTimer() {
        timerJob?.cancel()
    }

    private fun startUpdateTimer() {
        createUpdateTimer()
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
        binding.ivTrackImage.doOnNextLayout {
            val with = binding.root.width -2 * dpToPx(24f, this)
            Glide.with(this)
                .load(getCoverArtwork(track))
                .placeholder(R.drawable.placeholder_icon)
                .centerCrop()
                .override(with, with)
                .transform(RoundedCorners(dpToPx(8f, this)))
                .into(binding.ivTrackImage)
        }

        binding.tvTrackName.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.tvTrackTime.text = DateTimeUtil.timeFormat(track.trackTime.toInt())
        binding.tvAlbumName.text = track.collectionName
        binding.tvYearName.text = DateTimeUtil.dateFormat(track.releaseDate!!)
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
        private const val REFRESH_TRACK_TIMER_MILLIS = 300L

        fun newInstance(context: Context, track: String): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(TRACK, track)
            }
        }
    }
}