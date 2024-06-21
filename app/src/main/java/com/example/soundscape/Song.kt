package com.example.soundscape

data class Song(
   // val songId: String,
    val title: String,
    val artistName: String,
    //val release: String? = null,
    //val year: String? = null,
    var isFavorited: Boolean = false  // Track favorite status
)
