package com.example.playlistmaker.media.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.util.DateTimeUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    private var playlistId: Long? = null

    private var trackAdapter: TrackAdapter? = null

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel() {
        parametersOf(playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireArguments().getLong(ARGS_PLAYLIST).let {
            playlistId = it
            viewModel.updatePlaylistInfo(it)
        }

        binding.backButtonPlaylist.setOnClickListener { findNavController().navigateUp() }
        setupRecyclerView()
        setupShare()

        BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.getPlaylistInfo.observe(viewLifecycleOwner) { playlist ->
            initPlaylistInfo(playlist)
            setupBottomSheetMenu(playlist)
        }

        viewModel.getTrackList.observe(viewLifecycleOwner) { trackList ->
            binding.tvPlaylistDuration.text = getDurationPlaylist(trackList).plus(" минут")
            trackAdapter?.submitList(trackList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheetMenu(playlist: Playlist) {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.ivPlaylistSettings.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                binding.overlay.alpha = slideOffset
            }
        })

        with(binding) {
            if (playlist.image == null) {
                ivBottomSheetImage.setImageResource(R.drawable.placeholder_icon)
            } else {
                ivBottomSheetImage.setImageURI(loadImageUri(playlist.image))
            }
            tvBottomSheetPlaylistName.text = playlist.name
            tvBottomSheetPlaylistCount.text = playlist.trackCount.toString()
                .plus(" ")
                .plus(trackCountEnd(playlist.trackCount))

            tvBottomSheetPlaylistShare.setOnClickListener { sharePlaylist() }
            tvBottomSheetPlaylistDelete.setOnClickListener {
                BottomSheetBehavior.from(bottomSheetMenu).apply {
                    state = BottomSheetBehavior.STATE_HIDDEN
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_plalist))
                    .setMessage(getString(R.string.do_you_want_to_delete_a_playlist))
                    .setNeutralButton(getString(R.string.no)) { _, _ -> }
                    .setNegativeButton(getString(R.string.yes)) { _, _ ->
                        findNavController().navigateUp()
                        viewModel.deletePlaylist()
                    }
                    .show()
            }
            tvBottomSheetPlaylistEdit.setOnClickListener { launchEditPlaylistFragment(playlist) }
        }
    }

    private fun initPlaylistInfo(playlist: Playlist) {
        with(binding) {
            if (playlist.image == null) {
                ivPlaylistImage.setImageResource(R.drawable.placeholder_icon)
            } else {
                ivPlaylistImage.setImageURI(loadImageUri(playlist.image))
            }
            tvPlaylistName.text = playlist.name
            tvPlaylistDescription.text = playlist.description
            tvPlaylistTrackCount.text = playlist.trackCount.toString()
                .plus(" ")
                .plus(trackCountEnd(playlist.trackCount))
        }
    }

    private fun launchEditPlaylistFragment(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_editPlaylistFragment,
            EditPlaylistFragment.createArgs(playlist.playlistId)
        )
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter()
        binding.rvTracks.adapter = trackAdapter
        trackAdapter?.onTrackClickListener = { launchPlayerActivity(it) }
        trackAdapter?.onLongTrackClickListener = { deleteTrackFromPlaylist(it) }
    }

    private fun setupShare() {
        binding.ivPlaylistShare.setOnClickListener { sharePlaylist() }
    }

    private fun sharePlaylist() {
        if (trackAdapter!!.currentList.isNotEmpty()) {
            viewModel.sharingPlaylist()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_empty_playlist),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteTrackFromPlaylist(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.message_dialog))
            .setNeutralButton(getString(R.string.cancel_dialog)) { _, _ -> }
            .setNegativeButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteTrackFromPlaylist(track, trackAdapter!!.currentList)
            }
            .show()
    }

    private fun getDurationPlaylist(trackList: List<Track>): String {
        var duration = 0
        trackList.forEach {
            duration += it.trackTime.toInt()
        }

        return DateTimeUtil.playlistTimeFormat(duration)
    }

    private fun launchPlayerActivity(track: Track) {
        startActivity(
            PlayerActivity.newInstance(
                requireContext(),
                Gson().toJson(track)
            )
        )
    }

    private fun trackCountEnd(countTracks: Int): String {
        return requireContext().resources.getQuantityString(R.plurals.trackCountEnd, countTracks)
    }

    private fun loadImageUri(uriImage: String): Uri {
        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, uriImage)
        return file.toUri()
    }

    companion object {
        private const val ARGS_PLAYLIST = "ARGS_PLAYLIST"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST to playlistId)
    }
}