package com.example.soundscape
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler
    private lateinit var soundProgressBar: ProgressBar
    private lateinit var stopButton: Button
    private lateinit var songNameTextView: TextView
    private lateinit var songLengthTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize views
        soundProgressBar = findViewById(R.id.soundProgressBar)
        stopButton = findViewById(R.id.stopButton)
        songNameTextView = findViewById(R.id.songNameTextView)
        songLengthTextView = findViewById(R.id.songLengthTextView)

        val selectButton: Button = findViewById(R.id.selectButton)
        handler = Handler(mainLooper)

        selectButton.setOnClickListener {
            showSelectSoundDialog()
        }

        stopButton.setOnClickListener {
            stopPlayback()
        }
    }

    private fun showSelectSoundDialog() {
        val fileNames = arrayOf("Song 1", "Song 2", "Song 3")
        val fileResIds = arrayOf(R.raw.file1, R.raw.file2, R.raw.file3)

        AlertDialog.Builder(this)
            .setTitle("Select a Sound")
            .setItems(fileNames) { _, which ->
                playSound(fileResIds[which], fileNames[which])
            }
            .show()
    }

    private fun playSound(resId: Int, songName: String) {
        // Stop and release any existing mediaPlayer
        stopPlayback()

        val intent = Intent(this, CurrentlyPlayingActivity::class.java).apply {
            putExtra("SONG_NAME", songName)
            putExtra("SONG_DURATION", mediaPlayer?.duration ?: 0)
        }
        startActivity(intent)

        mediaPlayer = MediaPlayer.create(this, resId).apply {
            setOnCompletionListener {
                stopPlayback()
            }
            start()
        }

        // Prepare UI
        stopButton.visibility = View.VISIBLE
        songNameTextView.apply {
            text = songName
            visibility = View.VISIBLE
        }
        val totalDuration = mediaPlayer?.duration ?: 0 // Safely access duration
        val minutes = totalDuration / 60000
        val seconds = (totalDuration % 60000) / 1000
        songLengthTextView.apply {
            text = getString(R.string.song_length, minutes, seconds)
            visibility = View.VISIBLE
        }
        soundProgressBar.apply {
            max = totalDuration
            progress = 0
            visibility = View.VISIBLE
        }

        // Update progress bar
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        soundProgressBar.progress = it.currentPosition
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }, 1000)
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopButton.visibility = View.GONE
        songNameTextView.visibility = View.GONE
        songLengthTextView.visibility = View.GONE
        soundProgressBar.visibility = View.GONE
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }
}