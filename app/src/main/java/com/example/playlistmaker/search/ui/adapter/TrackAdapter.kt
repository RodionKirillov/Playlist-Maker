package com.example.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.view_holder.TrackViewHolder

class TrackAdapter : ListAdapter<Track, TrackViewHolder>(TrackItemDiffCallback()) {

    var onTrackClickListener: ((Track) -> Unit)? = null
    var onLongTrackClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onTrackClickListener?.invoke(item)
        }

        holder.itemView.setOnLongClickListener {
            onLongTrackClickListener?.invoke(item)
            true
        }
    }
}


