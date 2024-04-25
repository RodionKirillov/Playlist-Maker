package com.example.playlistmaker.search.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.search.domain.model.Track

class TrackItemDiffCallback : DiffUtil.ItemCallback<Track>() {

    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}