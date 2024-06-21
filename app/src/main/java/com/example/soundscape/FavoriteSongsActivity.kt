package com.example.soundscape

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteSongsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_songs_activity)

        val favoriteSongs = getFavoriteSongs()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FavoriteSongsAdapter(this, favoriteSongs)
    }

    private fun getFavoriteSongs(): List<Recommendation> {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<Recommendation>>() {}.type
        return gson.fromJson(sharedPreferences.getString("favoriteSongs", ""), type) ?: emptyList()
    }
}
