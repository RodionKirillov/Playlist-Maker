package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val trackList: ArrayList<Track>,
    private val onTrackClickListener: OnTrackClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.track_item,
            parent,
            false
        )
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onTrackClickListener.onTrackClick(trackList[position])
        }
    }

    override fun getItemCount(): Int = trackList.size
}

fun interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}


