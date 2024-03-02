package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val searchViewModel: SearchViewModel by viewModel()
    private var stringEditText = ""
    private val trackList = mutableListOf<Track>()
    private var clickDebounce = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchViewModel.observeState().observe(this) {
            render(it)
        }

        searchViewModel.observeClickDebounce().observe(this) {
            clickDebounce = it
        }

        searchAdapter = TrackAdapter { launchPlayerActivity(it) }
        binding.rvSearchTrack.adapter = searchAdapter

        historyAdapter = TrackAdapter { launchPlayerActivity(it) }
        binding.rvHistoryTrack.adapter = historyAdapter
        historyAdapter.setItems(getHistoryTracks())

        binding.bCleanHistory.setOnClickListener {
            clearHistoryButtonClicked()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.svHistoryTrack.visibility =
                if (hasFocus && binding.etSearch.text.isNullOrEmpty() && getHistoryTracks().size > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        binding.ivClearIcon.setOnClickListener {
            binding.etSearch.setText("")
            trackList.clear()
            searchAdapter.notifyDataSetChanged()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }


        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                stringEditText = p0.toString()
                binding.ivClearIcon.visibility = clearButtonVisibility(p0)
                binding.svHistoryTrack.visibility =
                    if (binding.etSearch.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE

                searchViewModel.searchDebounce(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        binding.etSearch.addTextChangedListener(searchTextWatcher)


        binding.bRefresh.setOnClickListener {
            searchViewModel.searchDebounce(stringEditText)   //TODO другой аргумент
        }

        if (savedInstanceState != null) {
            binding.etSearch.setText(savedInstanceState.getString(STRING_EDIT_TEXT))
        }

        binding.backButton.setNavigationOnClickListener {
            finish()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) { //Fragment созраняет своё состояние
        super.onSaveInstanceState(outState)
        outState.putString(STRING_EDIT_TEXT, stringEditText)
    }

    private fun clearHistoryButtonClicked() {
        binding.svHistoryTrack.visibility = View.GONE

        searchViewModel.clearTracks()

        binding.rvHistoryTrack.adapter?.notifyDataSetChanged()
    }

    private fun addTrackToSearchHistory(track: Track) {
        searchViewModel.saveTrack(track)

    }

    private fun launchPlayerActivity(track: Track) {
        if (clickDebounce) {
            searchViewModel.clickDebounceCheck()
            addTrackToSearchHistory(track)
            val intentPlayerActivity = Intent(this, PlayerActivity::class.java)
            intentPlayerActivity.putExtra(PlayerActivity.TRACK, Gson().toJson(track))
            startActivity(intentPlayerActivity)
        }
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

    companion object {
        private const val STRING_EDIT_TEXT = "STRING_EDIT_TEXT"
    }
}