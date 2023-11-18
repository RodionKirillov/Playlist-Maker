package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: ArrayList<Track>
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.track_item,
            parent,
            false
        )
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val roundedTrackImage = 5

        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)

        fun bind(model: Track) {
            trackName.text = model.trackName
            artistName.text = model.artistName.trim()
            trackTime.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()).format(model.trackTime.toInt()
                )
            Glide.with(itemView.context)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.placeholder_icon)
                .centerCrop()
                .transform(RoundedCorners(roundedTrackImage))
                .into(trackImage)
        }
    }
}

