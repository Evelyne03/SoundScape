package com.example.soundscape

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.soundscape.databinding.ItemSongBinding

class SongAdapter(private val songs: List<Song>, private val itemClickListener: (Song) -> Unit) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var filteredSongs: List<Song> = songs
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = filteredSongs[position]
        holder.binding.title.text = song.title
        holder.binding.artistName.text = song.artistName

        // Highlight the selected item
        val lightBlue = ContextCompat.getColor(holder.itemView.context, R.color.light_blue)
        holder.itemView.setBackgroundColor(
            if (selectedPosition == position) lightBlue
            else Color.TRANSPARENT
        )

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            itemClickListener(song)
        }
    }

    override fun getItemCount() = filteredSongs.size

    fun filter(query: String) {
        filteredSongs = if (query.length >= 3) {
            songs.filter { it.title.contains(query, ignoreCase = true) }
        } else {
            emptyList()
        }
        selectedPosition = RecyclerView.NO_POSITION // Clear selection when filtering
        notifyDataSetChanged()
    }

    fun getSelectedSong(): Song? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            filteredSongs[selectedPosition]
        } else {
            null
        }
    }
}
