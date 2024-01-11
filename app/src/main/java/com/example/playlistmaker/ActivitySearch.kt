package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivitySearch : AppCompatActivity() {

    private var stringEditText = ""

    private val baseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchAPI = retrofit.create(ITunesSearchAPI::class.java)

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

    private lateinit var sharedPrefsHistory: SharedPreferences
    private lateinit var listener: OnSharedPreferenceChangeListener

    private val trackList = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        sharedPrefsHistory = getSharedPreferences(SHARED_PREFS_HISTORY, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefsHistory)

        recyclerviewSearchTrack.adapter = TrackAdapter(trackList) {
            if (clickDebounce()) {
                searchHistory.addSearchHistory(it)
                launchPlayerActivity(it)
            }
        }

        recyclerviewHistoryTrack.adapter = TrackAdapter(searchHistory.getSearchHistory()) {
            if (clickDebounce()) {
                launchPlayerActivity(it)
            }
        }

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SHARED_PREFS_HISTORY_KEY) {
                val json = sharedPreferences.getString(SHARED_PREFS_HISTORY_KEY, null)
                if (json != null) {
                    recyclerviewHistoryTrack.adapter?.notifyDataSetChanged()
                }
            }
        }
        sharedPrefsHistory.registerOnSharedPreferenceChangeListener(listener)

        cleanHistoryButton.setOnClickListener {
            historyTrack.visibility = View.GONE
            searchHistory.clearSearchHistory()
            recyclerviewHistoryTrack.adapter?.notifyDataSetChanged()
        }


        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            historyTrack.visibility =
                if (hasFocus && editTextSearch.text.isNullOrEmpty() && searchHistory.getSearchHistory().size > 0) {
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

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_EDIT_TEXT, stringEditText)
    }

    private fun launchPlayerActivity(track: Track) {
        val intentPlayerActivity = Intent(this, PlayerActivity::class.java)
        intentPlayerActivity.putExtra(PlayerActivity.TRACK, Gson().toJson(track))
        startActivity(intentPlayerActivity)
    }

    private fun searchTracks() {
        if (stringEditText.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE

            iTunesSearchAPI.search(stringEditText)
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.isSuccessful) {
                            trackList.clear()
                            val responseTracks = response.body()?.results
                            if (responseTracks?.isNotEmpty() == true) {
                                trackList.addAll(responseTracks)

                                recyclerviewSearchTrack.visibility = View.VISIBLE
                                trackNotFound.visibility = View.GONE
                                internetError.visibility = View.GONE
                                progressBar.visibility = View.GONE

                                recyclerviewSearchTrack.adapter?.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                recyclerviewSearchTrack.visibility = View.GONE
                                trackNotFound.visibility = View.VISIBLE
                                internetError.visibility = View.GONE
                                progressBar.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        recyclerviewSearchTrack.visibility = View.GONE
                        trackNotFound.visibility = View.GONE
                        internetError.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE

                        refreshButton.setOnClickListener {
                            searchTracks()
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
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefsHistory.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val STRING_EDIT_TEXT = "STRING_EDIT_TEXT"
        const val SHARED_PREFS_HISTORY = "SHARED_PREFS_HISTORY"
    }
}