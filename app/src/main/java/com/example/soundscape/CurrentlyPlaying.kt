package com.example.soundscape

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class CurrentlyPlayingActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var songProgressBar: SeekBar
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    songProgressBar.progress = it.currentPosition
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currently_playing)

        val songNameTextView: TextView = findViewById(R.id.songNameNowPlaying)
        val songDurationTextView: TextView = findViewById(R.id.songDurationNowPlaying)
        val albumCoverImageView: ImageView = findViewById(R.id.albumCoverImageView)
        songProgressBar = findViewById(R.id.songProgressBar)

        val songName = intent.getStringExtra("SONG_NAME") ?: "Unknown"
        val songDuration = intent.getIntExtra("SONG_DURATION", 0)
        val songResId = intent.getIntExtra("SONG_RES_ID", -1)

        songNameTextView.text = songName
        songDurationTextView.text = formatDuration(songDuration)

        if (songResId != -1) {
            initializeMediaPlayer(songResId)
        } else {
            Log.e("CurrentlyPlaying", "Invalid song resource ID received")
        }

        albumCoverImageView.setImageResource(R.drawable.album_cover)

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopPlayback()
            songProgressBar.progress = 0
            mediaPlayer = null  // Ensure MediaPlayer is set to null after stopping
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }

        setupSeekBar()
    }

    private fun initializeMediaPlayer(resId: Int) {
        mediaPlayer = MediaPlayer.create(this, resId)?.apply {
            setOnCompletionListener {
                songProgressBar.progress = 0
                stopPlayback()
            }
            setOnErrorListener { _, what, extra ->
                Log.e("MediaPlayer", "Error occurred: What $what, Extra $extra")
                true
            }
            start()
            songProgressBar.max = duration
        }
        updateProgressBar()
    }

    private fun setupSeekBar() {
        songProgressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacks(updateSeekBarTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.postDelayed(updateSeekBarTask, 1000)
            }
        })
    }

    private fun updateProgressBar() {
        handler.post(updateSeekBarTask)
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()  // Make sure to release MediaPlayer
    }

    private fun formatDuration(durationMillis: Int): String {
        val minutes = durationMillis / 60000
        val seconds = (durationMillis % 60000) / 1000
        return getString(R.string.song_length, minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBarTask)
    }
}
