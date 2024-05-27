package com.example.playlistmaker.player.ui.view_holder

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistBottomSheetItemBinding
import com.example.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistBottomSheetViewHolder(
    private val binding: PlaylistBottomSheetItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        val count = model.trackCount
        binding.tvPlaylistCount.text = count.toString()
            .plus(" ")
            .plus(trackCountEnd(count))
        binding.tvPlaylistName.text = model.name

        if (!model.image.isNullOrEmpty()) {
            binding.ivPlaylistImage.setImageURI(loadImageUri(model.image))
        } else {
            binding.ivPlaylistImage.setImageResource(R.drawable.placeholder_icon)
        }
    }

    private fun trackCountEnd(countTracks: Int): String {
        return itemView.resources.getQuantityString(R.plurals.trackCountEnd, countTracks)
    }

    private fun loadImageUri(uriImage: String): Uri {
        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, uriImage)
        return file.toUri()
    }
}