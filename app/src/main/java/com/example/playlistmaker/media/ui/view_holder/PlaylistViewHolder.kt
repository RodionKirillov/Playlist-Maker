package com.example.playlistmaker.media.ui.view_holder

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: PlaylistItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        val count = model.trackCount
        binding.tvPlaylistCount.text = count.toString().plus(trackCountEnd(count))
        binding.tvPlaylistName.text = model.name

        if (!model.image.isNullOrEmpty()) {
            binding.ivPlaylistImage.setImageURI(loadImageUri(model.image))
        } else {
            binding.ivPlaylistImage.setImageResource(R.drawable.placeholder_icon)
        }
    }

    private fun trackCountEnd(countTracks: Int): String {
        return when {
            (countTracks % 20) in 10..20 || (countTracks % 10) in 5..9  || (countTracks % 10) == 0 -> " треков"
            (countTracks % 10) == 1 -> " трек"
            else -> " трека"
        }
    }

    private fun loadImageUri(uriImage: String): Uri {
        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, uriImage)
        return file.toUri()
    }
}