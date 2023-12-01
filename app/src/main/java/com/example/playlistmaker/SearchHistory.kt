package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import java.io.Serializable

const val SHARED_PREFS_HISTORY_KEY = "SHARED_PREFS_HISTORY_KEY"

class SearchHistory(
    private val sharedPreferences: SharedPreferences
) {
    fun addSearchHistory(track: Track) {
        val historyTrack: ArrayList<Track> = getSearchHistory()
        historyTrack.removeAll { it.trackId == track.trackId }
        historyTrack.add(0, track)

        if (historyTrack.size > 10) historyTrack.removeAt(historyTrack.size - 1)

        sharedPreferences.edit()
            .putString(SHARED_PREFS_HISTORY_KEY, createJsonFromTrack(historyTrack))
            .apply()
    }

    fun getSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(SHARED_PREFS_HISTORY_KEY, null) ?: return ArrayList()
        return createTracksFromJson(json)
    }

    private fun createTracksFromJson(json: String): ArrayList<Track> {
        val tracks = ArrayList<Track>()
        val gson = Gson()
        val result = gson.fromJson(json, JsonArray::class.java)
        for (track in result) {
            val track = gson.fromJson(track.toString(), Track::class.java)
            tracks.add(track)
        }
        return tracks
    }

    private fun createJsonFromTrack(tracksArray: ArrayList<Track>): String {
        return Gson().toJson(tracksArray)
    }
    fun clearSearchHistory() {
        sharedPreferences.edit().clear().apply()
    }
}
