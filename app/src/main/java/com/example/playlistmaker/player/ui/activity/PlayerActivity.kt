package com.example.playlistmaker.player.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private var playlistAdapter: PlaylistBottomSheetAdapter? = null
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel() {
        parametersOf(track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = getTrack()
        initTrackInfo(track)

        setupBottomSheet()
        setupOnClickListeners(savedInstanceState)

        viewModel.playerState.observe(this) { state ->
            observeState(state)
        }
    }

    private fun observeState(state: PlayerState) {
        when (state) {

            is PlayerState.Playing -> playingState(state.currentPosition)

            is PlayerState.Paused -> pauseState()

            is PlayerState.Completion, is PlayerState.Default, is PlayerState.Prepared -> {
                defaultState()
            }
        }
    }

    private fun defaultState() {
        binding.tvTrackTimePlay.text = getText(R.string.default_track_time)
        binding.ivPlayButton.setImageResource(R.drawable.play_button_light_theme)
    }

    private fun playingState(currentPosition: String) {
        binding.tvTrackTimePlay.text = currentPosition
        binding.ivPlayButton.setImageResource(R.drawable.pause_button_light_theme)
    }

    private fun pauseState() {
        binding.ivPlayButton.setImageResource(R.drawable.play_button_light_theme)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupOnClickListeners(savedInstanceState: Bundle?) {
        binding.backButtonPlayerActivity.setOnClickListener {
            finish()
        }

        binding.ivPlayButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlayLists()
    }

    private fun setupBottomSheet() {

        setupRecyclerView()

        viewModel.getPlaylistList.observe(this) { playlists ->
            playlistAdapter?.submitList(playlists)
        }

        viewModel.getContainsToPlaylist.observe(this) {
            showToast(it.first, it.second)
        }

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

    private fun initTrackInfo(track: Track) {
        binding.ivTrackImage.doOnNextLayout {
            val with = binding.root.width - 2 * dpToPx(24f, this)
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

    private fun getCoverArtwork(track: Track): String {
        return track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun getTrack(): Track {
        val json = intent.getStringExtra(TRACK)
        return Gson().fromJson(json, Track::class.java)
    }

    companion object {
        const val TRACK = "TRACK"

        fun newInstance(context: Context, track: String): Intent {
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(TRACK, track)
            }
        }
    }
}