package com.example.soundscape

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteSongsAdapter(private val context: Context, private val songList: MutableList<Recommendation>) : RecyclerView.Adapter<FavoriteSongsAdapter.ViewHolder>() {

    inner class ViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
        val songTitle: TextView = layout.findViewById(R.id.songTitle)
        val artistName: TextView = layout.findViewById(R.id.artistName)
        val deleteButton: Button = layout.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.favorite_song_item, parent, false) as LinearLayout
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList[position]
        holder.songTitle.text = song.song
        holder.artistName.text = song.artist

        holder.deleteButton.setOnClickListener {
            removeSongFromFavorites(song)
            songList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, songList.size)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    private fun removeSongFromFavorites(song: Recommendation) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type

        val favorites: MutableList<Recommendation> = gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: mutableListOf()
        favorites.removeIf { it.song == song.song && it.artist == song.artist }
        editor.putString("favoriteSongs", gson.toJson(favorites))
        editor.apply()
    }
}
