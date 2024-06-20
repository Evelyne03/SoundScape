package com.example.soundscape


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.soundscape.R
import com.example.soundscape.Song

class FavoriteSongsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_songs_activity)

        // Sample data
        val songList = listOf(
            Song("1", "Song 1", "Artist 1", "2021"),
            Song("2", "Song 2", "Artist 2", "2022"),
            Song("3", "Song 3", "Artist 3", "2023")
            // Add more songs as needed
        )

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set adapter
        recyclerView.adapter = FavoriteSongsAdapter(this, songList)
    }

}
