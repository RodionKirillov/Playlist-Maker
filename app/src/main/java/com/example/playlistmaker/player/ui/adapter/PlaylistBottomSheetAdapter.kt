package com.example.playlistmaker.player.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.databinding.PlaylistBottomSheetItemBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.adapter.PlaylistItemDiffCallback
import com.example.playlistmaker.player.ui.view_holder.PlaylistBottomSheetViewHolder

class PlaylistBottomSheetAdapter :
    ListAdapter<Playlist, PlaylistBottomSheetViewHolder>(PlaylistItemDiffCallback()) {

        var onPlaylistClickListener: ((Playlist) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistBottomSheetViewHolder {
       val binding = PlaylistBottomSheetItemBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false
       )
        return PlaylistBottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onPlaylistClickListener?.invoke(item)
        }
    }
}