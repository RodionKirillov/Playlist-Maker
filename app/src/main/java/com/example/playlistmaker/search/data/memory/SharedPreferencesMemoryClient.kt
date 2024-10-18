package com.example.playlistmaker.search.data.memory

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.source.MemoryClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesMemoryClient(
    private val sharedPrefsHistory: SharedPreferences
) : MemoryClient {

    override fun addSearchHistory(track: TrackDto) {
        val historyTack = getSearchHistory().toMutableList()
        historyTack.removeAll { it.trackId == track.trackId }
        historyTack.add(0, track)

        if (historyTack.size > MAX_HISTORY_SIZE) historyTack.removeAt(historyTack.size - 1)

        sharedPrefsHistory.edit()
            .putString(SHARED_PREFS_HISTORY_KEY, createJsonFromTrack(historyTack))
            .apply()
    }

    override fun getSearchHistory(): List<TrackDto> {
        val json =
            sharedPrefsHistory.getString(SHARED_PREFS_HISTORY_KEY, null) ?: return emptyList()
        return createTracksFromJson(json)
    }

    override fun clearSearchHistory() {
        sharedPrefsHistory.edit().clear().apply()
    }

    private fun createJsonFromTrack(tracksArray: List<TrackDto>): String {
        return Gson().toJson(tracksArray)
    }

    private fun createTracksFromJson(json: String): List<TrackDto> {
        val type = object : TypeToken<List<TrackDto>>() {}.type
        return Gson().fromJson(json, type)
    }

    companion object {
        const val SHARED_PREFS_HISTORY = "SHARED_PREFS_HISTORY"

        private const val SHARED_PREFS_HISTORY_KEY = "SHARED_PREFS_HISTORY_KEY"
        private const val MAX_HISTORY_SIZE = 10
    }
}

