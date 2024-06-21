package com.example.soundscape

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soundscape.Utils.songList
import com.example.soundscape.databinding.RecommenderActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://192.168.19.1:5100/"

class RecommenderActivity : AppCompatActivity() {

    private lateinit var binding: RecommenderActivityBinding
    private lateinit var adapter: SongAdapter
    private var filteredSongs = mutableListOf<Song>()
    private var selectedSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecommenderActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SongAdapter(filteredSongs) { song ->
            selectedSong = song
        }
        binding.recyclerView.adapter = adapter

        setupSearchBar()
        setupShowDetailsButton()
    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let { text ->
                    if (text.length >= 3) {
                        filterSongs(text.toString())
                    } else {
                        filteredSongs.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterSongs(query: String) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) {
                songList.filter { it.title.contains(query, ignoreCase = true) }
            }
            filteredSongs.clear()
            filteredSongs.addAll(result)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupShowDetailsButton() {
        binding.showDetailsButton.setOnClickListener {
            selectedSong?.let { song ->
                fetchRecommendations(song.title)
            } ?: run {
                Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchRecommendations(songTitle: String) {
        lifecycleScope.launch {
            try {
                val recommendations = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val api = retrofit.create(ApiService::class.java)
                    val response = api.getRecommendations(SongRequest(songTitle)).execute()
                    if (response.isSuccessful) {
                        response.body() ?: emptyList()
                    } else {
                        emptyList()
                    }
                }
                if (recommendations.isNotEmpty()) {
                    val parcelableRecommendations = ArrayList(recommendations)
                    val intent = Intent(this@RecommenderActivity, RelatedSongsActivity::class.java).apply {
                        putParcelableArrayListExtra("recommendedSongs", parcelableRecommendations)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@RecommenderActivity, "No recommendations found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RecommenderActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
