package com.example.playlistmaker.ui.search

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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.google.gson.Gson

class ActivitySearch : AppCompatActivity() {

    private lateinit var interactor: TracksInteractor

    private var stringEditText = ""

    private var isClickAllowed = true
    private val searchRunnable = Runnable { searchTracks() }
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var backButton: Toolbar
    private lateinit var editTextSearch: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerviewSearchTrack: RecyclerView
    private lateinit var recyclerviewHistoryTrack: RecyclerView
    private lateinit var historyTrack: ScrollView
    private lateinit var cleanHistoryButton: Button
    private lateinit var trackNotFound: LinearLayout
    private lateinit var internetError: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar

    private val trackList = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        interactor = Creator.provideTracksInteractor(this)

        initViews()

        recyclerviewSearchTrack.adapter = TrackAdapter(trackList) {
            if (clickDebounce()) {
                launchPlayerActivity(it)
                addTrackToSearchHistory(it)
            }
        }

        trackList.addAll(interactor.getTracks())
        recyclerviewHistoryTrack.adapter = TrackAdapter(trackList) {
            if (clickDebounce()) {
                launchPlayerActivity(it)
                addTrackToSearchHistory(it)
                recyclerviewHistoryTrack.adapter?.notifyDataSetChanged()
            }
        }

        cleanHistoryButton.setOnClickListener {
            historyTrack.visibility = View.GONE

            interactor.clearTracks()

            recyclerviewHistoryTrack.adapter?.notifyDataSetChanged()
        }


        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            historyTrack.visibility =
                if (hasFocus && editTextSearch.text.isNullOrEmpty() && interactor.getTracks().size > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        if (savedInstanceState != null) {
            editTextSearch.setText(savedInstanceState.getString(STRING_EDIT_TEXT))
        }

        clearIcon.setOnClickListener {
            editTextSearch.setText("")
            trackList.clear()
            recyclerviewSearchTrack.adapter?.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                stringEditText = p0.toString()
                clearIcon.visibility = clearButtonVisibility(p0)
                historyTrack.visibility =
                    if (editTextSearch.hasFocus() && p0?.isEmpty() == true) View.VISIBLE else View.GONE
                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        editTextSearch.addTextChangedListener(searchTextWatcher)

        refreshButton.setOnClickListener {
            searchTracks()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_EDIT_TEXT, stringEditText)
    }

    private fun addTrackToSearchHistory(track: Track) {
        interactor.saveTrack(track)

    }

    private fun launchPlayerActivity(track: Track) {
        val intentPlayerActivity = Intent(this, PlayerActivity::class.java)
        intentPlayerActivity.putExtra(PlayerActivity.TRACK, Gson().toJson(track))
        startActivity(intentPlayerActivity)
    }

    private fun searchTracks() {
        if (stringEditText.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE

            interactor.searchTracks(
                stringEditText,
                consumer = object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?) {
                        handler.post {
                            if (foundTracks == null) {

                                recyclerviewSearchTrack.visibility = View.GONE
                                trackNotFound.visibility = View.GONE
                                internetError.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE

                            } else {
                                if (foundTracks.isNotEmpty()) {
                                    trackList.clear()
                                    trackList.addAll(foundTracks)

                                    recyclerviewSearchTrack.visibility = View.VISIBLE
                                    trackNotFound.visibility = View.GONE
                                    internetError.visibility = View.GONE
                                    progressBar.visibility = View.GONE

                                    recyclerviewSearchTrack.adapter?.notifyDataSetChanged()
                                } else {
                                    recyclerviewSearchTrack.visibility = View.GONE
                                    trackNotFound.visibility = View.VISIBLE
                                    internetError.visibility = View.GONE
                                    progressBar.visibility = View.GONE
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        editTextSearch = findViewById(R.id.editTextSearch)
        clearIcon = findViewById(R.id.clear_icon)
        recyclerviewSearchTrack = findViewById(R.id.recyclerviewSearchTrack)
        recyclerviewHistoryTrack = findViewById(R.id.recyclerviewHistoryTrack)
        historyTrack = findViewById(R.id.historyTrack)
        cleanHistoryButton = findViewById(R.id.cleanHistoryButton)
        trackNotFound = findViewById(R.id.trackNotFound)
        internetError = findViewById(R.id.internetError)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val STRING_EDIT_TEXT = "STRING_EDIT_TEXT"
        const val SHARED_PREFS_HISTORY = "SHARED_PREFS_HISTORY"
    }
}