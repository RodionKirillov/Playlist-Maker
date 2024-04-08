package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.util.debounce
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val searchViewModel: SearchViewModel by viewModel()
    private val trackList = mutableListOf<Track>()

    private var editTextFocus = false
    private var stringEditText = ""
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            addTrackToSearchHistory(track)

            val intentPlayerActivity = Intent(requireContext(), PlayerActivity::class.java)//TODO Переделать интент
            intentPlayerActivity.putExtra(PlayerActivity.TRACK, Gson().toJson(track))
            startActivity(intentPlayerActivity)

            historyAdapter.setItems(getHistoryTracks())
        }

        searchViewModel.observeState().observe(viewLifecycleOwner) { render(it) }

        searchAdapter = TrackAdapter { onTrackClickDebounce(it) }
        binding.rvSearchTrack.adapter = searchAdapter

        historyAdapter = TrackAdapter { onTrackClickDebounce(it) }
        binding.rvHistoryTrack.adapter = historyAdapter
        historyAdapter.setItems(getHistoryTracks())

        binding.bCleanHistory.setOnClickListener {
            clearHistoryButtonClicked()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            editTextFocus = hasFocus
            binding.svHistoryTrack.visibility = historyVisibility(hasFocus)
        }

        binding.ivClearIcon.setOnClickListener {
            binding.etSearch.setText("")
            trackList.clear()
            searchAdapter.notifyDataSetChanged()
            searchViewModel.showHistory()

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
        }


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


        binding.bRefresh.setOnClickListener {
            searchViewModel.searchRequest(stringEditText)
        }

        if (savedInstanceState != null) {
            binding.etSearch.setText(savedInstanceState.getString(STRING_EDIT_TEXT))
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_EDIT_TEXT, stringEditText)
    }

    private fun clearHistoryButtonClicked() {
        searchViewModel.clearTracks()
        binding.svHistoryTrack.visibility = historyVisibility(editTextFocus)

        binding.rvHistoryTrack.adapter?.notifyDataSetChanged()
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

    private fun historyVisibility(hasFocus: Boolean): Int {
        return if (hasFocus && binding.etSearch.text.isNullOrEmpty() && getHistoryTracks().size > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
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

        trackList.clear()
        trackList.addAll(tracks)
        searchAdapter.setItems(trackList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val STRING_EDIT_TEXT = "STRING_EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}