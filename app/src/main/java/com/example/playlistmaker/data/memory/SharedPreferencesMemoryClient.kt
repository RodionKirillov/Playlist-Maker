package com.example.playlistmaker.data.memory

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.MemoryClient
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.ui.search.ActivitySearch.Companion.SHARED_PREFS_HISTORY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesMemoryClient(context: Context) : MemoryClient {

    private val sharedPrefsHistory =
        context.getSharedPreferences(SHARED_PREFS_HISTORY, MODE_PRIVATE)

    override fun addSearchHistory(trackArray: List<TrackDto>) {
        sharedPrefsHistory.edit()
            .putString(SHARED_PREFS_HISTORY_KEY, createJsonFromTrack(trackArray))
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

    companion object{
        const val SHARED_PREFS_HISTORY_KEY = "SHARED_PREFS_HISTORY_KEY"
    }
}

