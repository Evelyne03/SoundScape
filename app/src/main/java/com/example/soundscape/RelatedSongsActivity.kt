package com.example.soundscape

/*import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.soundscape.Song

class RelatedSongsActivity : AppCompatActivity() {

    private lateinit var songImage: ImageView
    private lateinit var songName: TextView
    private lateinit var artistName: TextView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var favoriteButton: ToggleButton

    private var currentSongIndex = 0
    private val songs = listOf(
        Song("Song 1", "Artist 1", "Release Info 1", "2021"),
        Song("Song 2", "Artist 2", "Release Info 2", "2022"),
        Song("Song 3", "Artist 3", "Release Info 3", "2023")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.related_songs_activity)

        favoriteButton = findViewById(R.id.favoriteButton)
        setupFavoriteButton()

        songImage = findViewById(R.id.songImage)
        songName = findViewById(R.id.songName)
        artistName = findViewById(R.id.artistName)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

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
    }

    private fun updateSongDetails() {
        val currentSong = songs[currentSongIndex]
        songName.text = currentSong.title
        artistName.text = currentSong.artistName

        // Remove listener before changing state to prevent it from firing due to programmatic change
        favoriteButton.setOnCheckedChangeListener(null)

        // Set checked state based on current song's favorite status
        favoriteButton.isChecked = currentSong.isFavorited

        // Re-add listener after changing state
        favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            // Update the current song's favorite status
            songs[currentSongIndex].isFavorited = isChecked

            if (isChecked) {
                Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setupFavoriteButton() {
        favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            // Update the current song's favorite status
            songs[currentSongIndex].isFavorited = isChecked

            if (isChecked) {
                Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }
}*/


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


