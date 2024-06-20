package com.example.soundscape

import android.content.Context
import com.example.soundscape.Song
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

fun loadCsv(context: Context): List<Song> {
    val inputStream = context.resources.openRawResource(R.raw.song_data)
    return csvReader().readAllWithHeader(inputStream).map { row ->
        Song(
            songId = row["song_id"] ?: "",
            title = row["title"] ?: "",
            release = row["release"] ?: "",
            artistName = row["artist_name"] ?: "",
            year = row["year"] ?: ""
        )
    }
}
