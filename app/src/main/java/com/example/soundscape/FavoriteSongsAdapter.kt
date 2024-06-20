package com.example.soundscape


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundscape.R
import com.example.soundscape.Song

class FavoriteSongsAdapter(private val context: Context, private val songList: List<Song>) : RecyclerView.Adapter<FavoriteSongsAdapter.ViewHolder>() {

    inner class ViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LinearLayout(context)
        layout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)

        val songTitle = TextView(context)
        songTitle.layoutParams = params
        songTitle.textSize = 18f
        songTitle.setTextColor(Color.BLACK)
        layout.addView(songTitle)

        val artistName = TextView(context)
        artistName.layoutParams = params
        artistName.textSize = 16f
        artistName.setTextColor(Color.GRAY)
        layout.addView(artistName)

        val releaseYear = TextView(context)
        releaseYear.layoutParams = params
        releaseYear.textSize = 14f
        releaseYear.setTextColor(Color.LTGRAY)
        layout.addView(releaseYear)

        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList[position]
        val layout = holder.layout

        (layout.getChildAt(0) as TextView).text = song.title
        (layout.getChildAt(1) as TextView).text = song.artistName
        (layout.getChildAt(2) as TextView).text = song.year ?: ""
    }

    override fun getItemCount(): Int {
        return songList.size
    }
}
