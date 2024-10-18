package com.example.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media.ui.model.FavoriteState
import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.util.BindingFragment
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BindingFragment<FragmentFavoriteTracksBinding>() {

    private lateinit var favoriteAdapter: TrackAdapter

    private val viewModel: FavoriteTracksViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoritesTrack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.getState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setupRecyclerView() {
        favoriteAdapter = TrackAdapter()
        binding.recyclerViewFavoriteTracks.adapter = favoriteAdapter
        favoriteAdapter.onTrackClickListener = { launchPlayerActivity(it) }
    }

    private fun launchPlayerActivity(track: Track) {
        if (clickDebounce()) {
            startActivity(
                PlayerActivity.newInstance(
                    requireContext(),
                    Gson().toJson(track)
                )
            )
        }
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Empty -> showEmpty()
            is FavoriteState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.recyclerViewFavoriteTracks.visibility = View.GONE
        binding.trackNotFound.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerViewFavoriteTracks.visibility = View.VISIBLE
        binding.trackNotFound.visibility = View.GONE

        favoriteAdapter.submitList(tracks)
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}