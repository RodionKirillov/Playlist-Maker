package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import java.io.Serializable

const val SHARED_PREFS_HISTORY_KEY = "SHARED_PREFS_HISTORY_KEY"
const val maxHistoryItem = 10

class SearchHistory(
    private val sharedPreferences: SharedPreferences
) {
    fun addSearchHistory(track: Track) {
        val historyTrack: ArrayList<Track> = getSearchHistory()
        historyTrack.removeAll { it.trackId == track.trackId }
        historyTrack.add(0, track)

        if (historyTrack.size > maxHistoryItem) historyTrack.removeAt(historyTrack.size - 1)

        sharedPreferences.edit()
            .putString(SHARED_PREFS_HISTORY_KEY, createJsonFromTrack(historyTrack))
            .apply()
    }

    fun getSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(SHARED_PREFS_HISTORY_KEY, null) ?: return ArrayList()
        return createTracksFromJson(json)
    }

    private fun createTracksFromJson(json: String): ArrayList<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun createJsonFromTrack(tracksArray: ArrayList<Track>): String {
        return Gson().toJson(tracksArray)
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().clear().apply()
    }
}
