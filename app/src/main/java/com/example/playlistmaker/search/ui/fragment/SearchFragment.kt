package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.util.BindingFragment
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val searchViewModel: SearchViewModel by viewModel()

    private var editTextFocus = false
    private var stringEditText = ""

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.observeState().observe(viewLifecycleOwner) { render(it) }
        setupRecyclerView()
        setupOnClickListeners()
        setupTextChangeListener()

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            editTextFocus = hasFocus
            binding.svHistoryTrack.visibility = historyVisibility(hasFocus)
        }
    }

    private fun setupTextChangeListener() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                stringEditText = p0.toString()
                binding.ivClearIcon.visibility = clearButtonVisibility(p0)
                binding.svHistoryTrack.visibility =
                    if (binding.etSearch.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE

                searchViewModel.searchDebounce(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etSearch.addTextChangedListener(searchTextWatcher)
    }

    private fun setupOnClickListeners() {
        binding.bCleanHistory.setOnClickListener {
            clearHistoryButtonClicked()
        }

        binding.ivClearIcon.setOnClickListener {
            binding.etSearch.setText("")
            searchViewModel.showHistory()

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
        }

        binding.bRefresh.setOnClickListener {
            searchViewModel.searchRequest(stringEditText)
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = TrackAdapter()
        binding.rvSearchTrack.adapter = searchAdapter
        searchAdapter.onTrackClickListener = { launchPlayerActivity(it) }

        historyAdapter = TrackAdapter()
        binding.rvHistoryTrack.adapter = historyAdapter
        historyAdapter.onTrackClickListener = { launchPlayerActivity(it) }
        historyAdapter.submitList(getHistoryTracks())
    }

    private fun launchPlayerActivity(track: Track) {
        if (clickDebounce()) {
            addTrackToSearchHistory(track)

            startActivity(
                PlayerActivity.newInstance(
                    requireContext(),
                    Gson().toJson(track)
                )
            )

            historyAdapter.submitList(getHistoryTracks())
        }
    }

    private fun clearHistoryButtonClicked() {
        searchViewModel.clearTracks()
        binding.svHistoryTrack.visibility = historyVisibility(editTextFocus)
    }

    private fun addTrackToSearchHistory(track: Track) {
        searchViewModel.saveTrack(track)
    }

    private fun getHistoryTracks(): List<Track> {
        return searchViewModel.getHistoryTracks()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun historyVisibility(hasFocus: Boolean): Int {
        return if (hasFocus && binding.etSearch.text.isNullOrEmpty() && getHistoryTracks().size > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty()
            is SearchState.Error -> showError()
            is SearchState.Loading -> showLoading()
            is SearchState.ShowHistory -> showHistory()
        }
    }

    private fun showHistory() {
        binding.rvSearchTrack.visibility = View.GONE
        binding.llTrackNotFound.visibility = View.GONE
        binding.llInternetError.visibility = View.GONE
        binding.pbLoading.visibility = View.GONE

        binding.svHistoryTrack.visibility = historyVisibility(editTextFocus)
    }

    private fun showLoading() {
        binding.rvSearchTrack.visibility = View.GONE
        binding.llTrackNotFound.visibility = View.GONE
        binding.llInternetError.visibility = View.GONE
        binding.pbLoading.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.rvSearchTrack.visibility = View.GONE
        binding.llTrackNotFound.visibility = View.GONE
        binding.llInternetError.visibility = View.VISIBLE
        binding.pbLoading.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.rvSearchTrack.visibility = View.GONE
        binding.llTrackNotFound.visibility = View.VISIBLE
        binding.llInternetError.visibility = View.GONE
        binding.pbLoading.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.rvSearchTrack.visibility = View.VISIBLE
        binding.llTrackNotFound.visibility = View.GONE
        binding.llInternetError.visibility = View.GONE
        binding.pbLoading.visibility = View.GONE

        searchAdapter.submitList(tracks)
    }
}