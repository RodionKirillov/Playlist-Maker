package com.example.playlistmaker.media.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.media.domain.model.Playlist

class PlaylistItemDiffCallback: DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.playlistId == newItem.playlistId
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}