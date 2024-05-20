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
import com.google.gson.reflect.TypeToken
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
        playlistId = requireArguments().getLong(ARGS_PLAYLIST)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonPlaylist.setOnClickListener { findNavController().navigateUp() }
        viewModel.updatePlaylistInfo(playlistId!!)
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

    override fun onDestroy() {
        super.onDestroy()
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

        if (playlist.image == null) {
            binding.ivBottomSheetImage.setImageResource(R.drawable.placeholder_icon)
        } else {
            binding.ivBottomSheetImage.setImageURI(loadImageUri(playlist.image))
        }
        binding.tvBottomSheetPlaylistName.text = playlist.name
        binding.tvBottomSheetPlaylistCount.text = playlist.trackCount.toString()
            .plus(trackCountEnd(playlist.trackCount))

        binding.tvBottomSheetPlaylistShare.setOnClickListener { sharePlaylist() }
        binding.tvBottomSheetPlaylistDelete.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
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
        binding.tvBottomSheetPlaylistEdit.setOnClickListener { launchEditPlaylistFragment(playlist) }
    }

    private fun initPlaylistInfo(playlist: Playlist) {
        if (playlist.image == null) {
            binding.ivPlaylistImage.setImageResource(R.drawable.placeholder_icon)
        } else {
            binding.ivPlaylistImage.setImageURI(loadImageUri(playlist.image))
        }
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistDescription.text = playlist.description
        binding.tvPlaylistTrackCount.text = playlist.trackCount.toString()
            .plus(trackCountEnd(playlist.trackCount))
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
                viewModel.deleteTrackFromPlaylist(track, trackAdapter!!.currentList.reversed())
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
        return when {
            (countTracks % 20) in 10..20 || (countTracks % 10) in 5..9 || (countTracks % 10) == 0 -> " треков"
            (countTracks % 10) == 1 -> " трек"
            else -> " трека"
        }
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