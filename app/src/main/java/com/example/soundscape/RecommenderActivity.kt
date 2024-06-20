package com.example.soundscape

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ListItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.example.soundscape.Song
import com.example.soundscape.databinding.RecommenderActivityBinding

class RecommenderActivity : AppCompatActivity() {

    private lateinit var binding: RecommenderActivityBinding
    private lateinit var adapter: SongAdapter
    private lateinit var songs: List<Song>
    private var selectedSong: Song? = null
    private var filteredSongs = mutableListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecommenderActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songs = loadCsv(this)
        adapter = SongAdapter(filteredSongs) { song ->
            selectedSong = song
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let { text ->
                    if (text.length >= 3) {
                        filteredSongs.clear()
                        filteredSongs.addAll(songs.filter { it.title.contains(text.toString(), ignoreCase = true) })
                    } else {
                        filteredSongs.clear()
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.showDetailsButton.setOnClickListener {
            val intent = Intent(this, RelatedSongsActivity::class.java)
            selectedSong?.let { song ->
                intent.putExtra("songTitle", song.title)
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}