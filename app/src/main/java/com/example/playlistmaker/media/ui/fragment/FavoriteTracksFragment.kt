package com.example.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media.ui.model.FavoriteState
import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.fragment.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var favoriteAdapter: TrackAdapter? = null

    private var isClickAllowed = true

    private var _binding: FragmentFavoriteTracksBinding? = null

    private val binding get() = _binding!!

    private val trackList = mutableListOf<Track>()

    private val viewModel: FavoriteTracksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoritesTrack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = TrackAdapter { launchPlayerActivity(it) }
        binding.recyclerViewFavoriteTracks.adapter = favoriteAdapter

        viewModel.getState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        favoriteAdapter = null
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
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

        trackList.clear()
        trackList.addAll(tracks)
        favoriteAdapter?.setItems(tracks)
    }


    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}