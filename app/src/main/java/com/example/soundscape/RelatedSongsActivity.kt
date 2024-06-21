package com.example.soundscape

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RelatedSongsActivity : AppCompatActivity() {

    private lateinit var songNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var favoriteButton: ToggleButton
    private lateinit var youtubeButton: Button

    private lateinit var songs: ArrayList<Recommendation>
    private var currentSongIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.related_songs_activity)

        songNameTextView = findViewById(R.id.songName)
        artistNameTextView = findViewById(R.id.artistName)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        favoriteButton = findViewById(R.id.favoriteButton)
        youtubeButton = findViewById(R.id.youtubeButton)

        songs = intent.getParcelableArrayListExtra("recommendedSongs") ?: ArrayList()

        if (songs.isNotEmpty()) {
            updateSongDetails()

            prevButton.setOnClickListener {
                if (currentSongIndex > 0) {
                    currentSongIndex--
                    updateSongDetails()
                }
            }

            nextButton.setOnClickListener {
                if (currentSongIndex < songs.size - 1) {
                    currentSongIndex++
                    updateSongDetails()
                }
            }

            favoriteButton.setOnCheckedChangeListener { _, isChecked ->
                songs[currentSongIndex].isFavorited = isChecked
                if (isChecked) {
                    addSongToFavorites(songs[currentSongIndex])
                    Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
                } else {
                    removeSongFromFavorites(songs[currentSongIndex])
                    Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
                }
            }

            youtubeButton.setOnClickListener {
                val currentSong = songs[currentSongIndex]
                val query = "${currentSong.song} ${currentSong.artist}"
                val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query"))
                startActivity(youtubeIntent)
            }

        } else {
            Toast.makeText(this, "No recommended songs found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateSongDetails() {
        val currentSong = songs[currentSongIndex]
        songNameTextView.text = currentSong.song
        artistNameTextView.text = currentSong.artist

        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.isChecked = isSongInFavorites(currentSong)

        favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            songs[currentSongIndex].isFavorited = isChecked
            if (isChecked) {
                addSongToFavorites(songs[currentSongIndex])
                Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                removeSongFromFavorites(songs[currentSongIndex])
                Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isSongInFavorites(song: Recommendation): Boolean {
        val sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type

        val favorites: MutableList<Recommendation> = gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: mutableListOf()
        return favorites.any { it.song == song.song && it.artist == song.artist }
    }

    private fun addSongToFavorites(song: Recommendation) {
        val sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type

        val favorites: MutableList<Recommendation> = gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: mutableListOf()

        // Check if the song is already in the favorites list
        if (favorites.any { it.song == song.song && it.artist == song.artist }) {
            Toast.makeText(this, "Song is already in favorites", Toast.LENGTH_SHORT).show()
            return
        }

        favorites.add(song)
        editor.putString("favoriteSongs", gson.toJson(favorites))
        editor.apply()
    }

    private fun removeSongFromFavorites(song: Recommendation) {
        val sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type

        val favorites: MutableList<Recommendation> = gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: mutableListOf()
        favorites.removeIf { it.song == song.song && it.artist == song.artist }
        editor.putString("favoriteSongs", gson.toJson(favorites))
        editor.apply()
    }
}
