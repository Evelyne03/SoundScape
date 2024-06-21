package com.example.soundscape

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.soundscape.Recommendation
import java.util.*

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

        // Initialize views
        songNameTextView = findViewById(R.id.songName)
        artistNameTextView = findViewById(R.id.artistName)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        favoriteButton = findViewById(R.id.favoriteButton)
        youtubeButton = findViewById(R.id.youtubeButton)

        // Retrieve recommended songs from intent extras
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
                    Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
                } else {
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

        // Remove listener before changing state to prevent it from firing due to programmatic change
        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.isChecked = currentSong.isFavorited

        // Re-add listener after changing state
        favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            songs[currentSongIndex].isFavorited = isChecked
            if (isChecked) {
                Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
