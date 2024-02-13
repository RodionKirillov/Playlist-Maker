package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var stringEditText = ""
    private val trackList = mutableListOf<Track>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        searchViewModel.observeState().observe(this) {
            render(it)
        }

        searchAdapter = TrackAdapter { launchPlayerActivity(it) }
        binding.recyclerviewSearchTrack.adapter = searchAdapter

        historyAdapter = TrackAdapter { launchPlayerActivity(it) }
        binding.recyclerviewHistoryTrack.adapter = historyAdapter
        historyAdapter.setItems(getHistoryTracks())

        binding.cleanHistoryButton.setOnClickListener {
            clearHistoryButtonClicked()
        }

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.historyTrack.visibility =
                if (hasFocus && binding.editTextSearch.text.isNullOrEmpty() && getHistoryTracks().size > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        binding.clearIcon.setOnClickListener {
            binding.editTextSearch.setText("")
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
                binding.clearIcon.visibility = clearButtonVisibility(p0)
                binding.historyTrack.visibility =
                    if (binding.editTextSearch.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE

                searchViewModel.searchDebounce(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        binding.editTextSearch.addTextChangedListener(searchTextWatcher)


        binding.refreshButton.setOnClickListener {
            searchViewModel.searchDebounce(stringEditText)   //TODO другой аргумент
        }

        if (savedInstanceState != null) {
            binding.editTextSearch.setText(savedInstanceState.getString(STRING_EDIT_TEXT))
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_EDIT_TEXT, stringEditText)
    }

    private fun clearHistoryButtonClicked() {
        binding.historyTrack.visibility = View.GONE

        searchViewModel.clearTracks()

        binding.recyclerviewHistoryTrack.adapter?.notifyDataSetChanged()
    }

    private fun addTrackToSearchHistory(track: Track) {
        searchViewModel.saveTrack(track)

    }

    private fun launchPlayerActivity(track: Track) {
        if (clickDebounce()) {
            addTrackToSearchHistory(track)
            val intentPlayerActivity = Intent(this, PlayerActivity::class.java)
            intentPlayerActivity.putExtra(PlayerActivity.TRACK, Gson().toJson(track))
            startActivity(intentPlayerActivity)
        }
    }

    private fun clickDebounce(): Boolean {
        return searchViewModel.clickDebounce()
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
            is SearchState.Empty -> showEmpty()   //TODO исправить String который сюда приходит
            is SearchState.Error -> showError()
            is SearchState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.recyclerviewSearchTrack.visibility = View.GONE
        binding.trackNotFound.visibility = View.GONE
        binding.internetError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.recyclerviewSearchTrack.visibility = View.GONE
        binding.trackNotFound.visibility = View.GONE
        binding.internetError.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.recyclerviewSearchTrack.visibility = View.GONE
        binding.trackNotFound.visibility = View.VISIBLE
        binding.internetError.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerviewSearchTrack.visibility = View.VISIBLE
        binding.trackNotFound.visibility = View.GONE
        binding.internetError.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        trackList.clear()
        trackList.addAll(tracks)
        searchAdapter.setItems(trackList)
    }

    companion object {
        private const val STRING_EDIT_TEXT = "STRING_EDIT_TEXT"
    }
}