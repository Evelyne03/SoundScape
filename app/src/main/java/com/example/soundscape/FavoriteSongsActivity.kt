package com.example.soundscape

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteSongsActivity : AppCompatActivity() {

    private lateinit var songsContainer: LinearLayout
    private lateinit var favoriteSongs: MutableList<Recommendation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_songs_activity)

        songsContainer = findViewById(R.id.songsContainer)
        favoriteSongs = getFavoriteSongs().toMutableList()

        displayFavoriteSongs()
    }

    private fun getFavoriteSongs(): List<Recommendation> {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type
        return gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: emptyList()
    }

    private fun displayFavoriteSongs() {
        val inflater = LayoutInflater.from(this)
        for (song in favoriteSongs) {
            val songView = inflater.inflate(R.layout.favorite_song_item, songsContainer, false)
            val songTitle = songView.findViewById<TextView>(R.id.songTitle)
            val artistName = songView.findViewById<TextView>(R.id.artistName)
            val deleteButton = songView.findViewById<Button>(R.id.deleteButton)

            songTitle.text = song.song
            artistName.text = song.artist

            deleteButton.setOnClickListener {
                removeSongFromFavorites(song)
                songsContainer.removeView(songView)
                Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
            }

            songsContainer.addView(songView)
        }
    }

    private fun removeSongFromFavorites(song: Recommendation) {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type

        val favorites: MutableList<Recommendation> = gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: mutableListOf()
        favorites.removeIf { it.song == song.song && it.artist == song.artist }
        editor.putString("favoriteSongs", gson.toJson(favorites))
        editor.apply()
    }
}
