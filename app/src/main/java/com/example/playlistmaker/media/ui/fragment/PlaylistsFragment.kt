package com.example.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.adapter.PlaylistAdapter
import com.example.playlistmaker.media.ui.model.PlaylistState
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var playlistAdapter: PlaylistAdapter? = null

    private var _binding: FragmentPlaylistsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.getState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    override fun onDestroyView() {
        _binding = null
        playlistAdapter = null
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter()
        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = playlistAdapter

        playlistAdapter?.onPlaylistClickListener = { playlist ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlist.playlistId)
            )
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty()
            is PlaylistState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        binding.rvPlaylists.visibility = View.GONE
        binding.llPlaylistsNotFound.visibility = View.VISIBLE

    }

    private fun showContent(playlists: List<Playlist>) {
        binding.rvPlaylists.visibility = View.VISIBLE
        binding.llPlaylistsNotFound.visibility = View.GONE

        playlistAdapter?.submitList(playlists)
    }


    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}