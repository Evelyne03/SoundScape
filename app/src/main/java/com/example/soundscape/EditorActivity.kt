package com.example.soundscape

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.widget.ImageView
import kotlin.random.Random


class EditorActivity : AppCompatActivity() {

    private var pitchValue: Int = 0
    private var speedValue: Float = 1.0f
    private var reverbValue: Int = 0
    private var totalDuration: Int = 0
    private lateinit var lowPitch: Button
    private lateinit var highPitch: Button
    private lateinit var playButton: Button
    private lateinit var stopButton: Button
    private lateinit var changesButton: Button
    private lateinit var fileSelector: Button
    private lateinit var discardButton: Button
    private lateinit var uri: Uri
    private lateinit var originalUri: Uri
    private lateinit var soundText: TextView
    private lateinit var pitchText: TextView
    private lateinit var speedText: TextView
    private lateinit var reverbText: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var listenedDurationTextView: TextView
    private lateinit var songDurationTextView: TextView
    private var mediaPlayer: MediaPlayer? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private lateinit var newUri: Uri
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        lowPitch = findViewById(R.id.lowerPitch)
        highPitch = findViewById(R.id.increasePitch)
        playButton = findViewById(R.id.playButton)
        stopButton = findViewById(R.id.stopButton)
        changesButton = findViewById(R.id.applyButton)
        discardButton = findViewById(R.id.discardButton)
        fileSelector = findViewById(R.id.fileButton)
        soundText = findViewById(R.id.songNameTextView)
        pitchText = findViewById(R.id.pitchValue)
        speedText = findViewById(R.id.speedValue)
        reverbText = findViewById(R.id.reverbValue)
        seekBar = findViewById(R.id.soundProgressBar)
        listenedDurationTextView = findViewById(R.id.listenedDurationTextView)
        songDurationTextView = findViewById(R.id.songDurationTextView)
        handler = Handler()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    updateDuration(progress, mediaPlayer?.duration ?: 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                seekBar.progress = it.currentPosition
                updateDuration(it.currentPosition, it.duration)
                handler.postDelayed(this, 100)
            }
        }
    }


    fun openFileManager(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            uri = data?.data!! // Update the uri variable with the newly selected file's URI
            originalUri = uri // Store the original URI
            soundText.text = getFileName(uri)
            newUri = Uri.EMPTY // Reset newUri when a new file is selected

            // Release the existing MediaPlayer if it's playing
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null

            // Get the duration of the selected audio file
            val tempMediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                prepare()
            }
            totalDuration = tempMediaPlayer.duration
            tempMediaPlayer.release()
            updateDuration(0, totalDuration)

            // Reset the SeekBar
            seekBar.progress = 0
            updateDuration(0, totalDuration)

            Toast.makeText(this, "File selected: ${getFileName(uri)}", Toast.LENGTH_SHORT).show()
        }
    }





    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    fun modifyPitch(view: View) {
        if (!::uri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("prefix", "suffix", cacheDir) // Temporary file in cache directory
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("audio/wav".toMediaType()))
            .addFormDataPart("pitch_change", pitchValue.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.19.1:5000/change_pitch")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(applicationContext, "Error: Failed to connect to server :(", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val outputFile = File(getExternalFilesDir(null), "modified_output.wav")
                        outputFile.outputStream().use { fileOut ->
                            response.body?.byteStream()?.copyTo(fileOut)
                        }
                        newUri = Uri.fromFile(outputFile)
                        runOnUiThread { Toast.makeText(applicationContext, "File modified and saved", Toast.LENGTH_SHORT).show() }
                    } else {
                        runOnUiThread { Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }

    fun modifySpeed(view: View) {
        if (!::uri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("prefix", "suffix", cacheDir) // Temporary file in cache directory
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("audio/wav".toMediaType()))
            .addFormDataPart("speed_change", speedValue.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.19.1:5000/change_speed")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(applicationContext, "Error: Failed to connect to server :(", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val outputFile = File(getExternalFilesDir(null), "modified_output.wav")
                        outputFile.outputStream().use { fileOut ->
                            response.body?.byteStream()?.copyTo(fileOut)
                        }
                        newUri = Uri.fromFile(outputFile)
                        runOnUiThread { Toast.makeText(applicationContext, "File modified and saved", Toast.LENGTH_SHORT).show() }
                    } else {
                        runOnUiThread { Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }

    fun modifyReverb(view: View) {
        if (!::uri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("prefix", "suffix", cacheDir) // Temporary file in cache directory
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("audio/wav".toMediaType()))
            .addFormDataPart("reverb_change", reverbValue.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.19.1:5000/change_reverb")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(applicationContext, "Error: Failed to connect to server :(", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val outputFile = File(getExternalFilesDir(null), "modified_output.wav")
                        outputFile.outputStream().use { fileOut ->
                            response.body?.byteStream()?.copyTo(fileOut)
                        }
                        newUri = Uri.fromFile(outputFile)
                        runOnUiThread { Toast.makeText(applicationContext, "File modified and saved", Toast.LENGTH_SHORT).show() }
                    } else {
                        runOnUiThread { Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }

    fun applyChanges(view: View) {
        if (!::uri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if any changes were made
        if (pitchValue == 0 && speedValue == 1.0f && reverbValue == 0) {
            Toast.makeText(this, "No changes to apply", Toast.LENGTH_SHORT).show()
            return
        }

        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("prefix", "suffix", cacheDir) // Temporary file in cache directory
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("audio/wav".toMediaType()))
            .addFormDataPart("pitch_change", pitchValue.toString())
            .addFormDataPart("speed_change", speedValue.toString())
            .addFormDataPart("reverb_change", reverbValue.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.19.1:5000/apply_changes")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(applicationContext, "Error: Failed to connect to server :(", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val outputFile = File(getExternalFilesDir(null), "modified_output.wav")
                        outputFile.outputStream().use { fileOut ->
                            response.body?.byteStream()?.copyTo(fileOut)
                        }
                        newUri = Uri.fromFile(outputFile)
                        runOnUiThread { Toast.makeText(applicationContext, "File modified and saved", Toast.LENGTH_SHORT).show() }
                    } else {
                        runOnUiThread { Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }

    fun denoiseAudio(view: View) {
        if (!::uri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("denoise", ".wav", cacheDir)
            tempFile.outputStream().use { output ->
                inputStream?.copyTo(output)
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("audio/wav".toMediaType()))
                .build()

            val request = Request.Builder()
                .url("http://192.168.19.1:5000/denoise_audio")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error: Failed to connect to server", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val outputFile = File(getExternalFilesDir(null), "denoised_output.wav")
                            outputFile.outputStream().use { fileOut ->
                                response.body?.byteStream()?.copyTo(fileOut)
                            }
                            val denoisedUri = Uri.fromFile(outputFile)
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Audio denoised and saved", Toast.LENGTH_SHORT).show()
                                // Update the newUri with the denoised file URI
                                newUri = denoisedUri
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }



    private fun applyEffects(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("prefix", "suffix", cacheDir)
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", tempFile.name, tempFile.asRequestBody("audio/wav".toMediaType()))
            .addFormDataPart("pitch_change", pitchValue.toString())
            .addFormDataPart("speed_change", speedValue.toString())
            .addFormDataPart("reverb_change", reverbValue.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.19.1:5000/apply_changes")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(applicationContext, "Error: Failed to connect to server :(", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val outputFile = File(getExternalFilesDir(null), "final_output.wav")
                        outputFile.outputStream().use { fileOut ->
                            response.body?.byteStream()?.copyTo(fileOut)
                        }
                        newUri = Uri.fromFile(outputFile)
                        runOnUiThread { Toast.makeText(applicationContext, "Effects applied and saved", Toast.LENGTH_SHORT).show() }
                    } else {
                        runOnUiThread { Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }

    fun discardChanges(view: View) {
        pitchValue = 0
        speedValue = 1.0f
        reverbValue = 0
        pitchText.text = pitchValue.toString()
        speedText.text = String.format("%.1fx", speedValue)
        reverbText.text = reverbValue.toString()

        // Stop the media player and reset to original file
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        newUri = Uri.EMPTY
        seekBar.progress = 0
        updateDuration(0, 0)

        Toast.makeText(this, "Changes discarded", Toast.LENGTH_SHORT).show()
    }

    fun playAudio(view: View) {
        val audioUri = if (::newUri.isInitialized && newUri != Uri.EMPTY) newUri else originalUri

        // If MediaPlayer is null or not playing, create and start it
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, audioUri)
                prepare()
                start()
            }
            seekBar.max = mediaPlayer!!.duration
            handler.post(updateSeekBar)
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
            handler.post(updateSeekBar)
        }

        updateDuration(mediaPlayer!!.currentPosition, mediaPlayer!!.duration)
    }

    fun stopAudio(view: View) {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
            handler.removeCallbacks(updateSeekBar)
        }
    }



    fun decreasePitch(view: View) {
        pitchValue--
        if (pitchValue < -3) {
            pitchValue = -3
        }
        pitchText.text = pitchValue.toString()
    }

    fun increasePitch(view: View) {
        pitchValue++
        if (pitchValue > 3) {
            pitchValue = 3
        }
        pitchText.text = pitchValue.toString()
    }

    fun decreaseSpeed(view: View) {
        speedValue -= 0.1f
        if (speedValue < 0.5f) {
            speedValue = 0.5f
        }
        speedText.text = String.format("%.1fx", speedValue)
    }

    fun increaseSpeed(view: View) {
        speedValue += 0.1f
        if (speedValue > 2.0f) {
            speedValue = 2.0f
        }
        speedText.text = String.format("%.1fx", speedValue)
    }

    fun decreaseReverb(view: View) {
        reverbValue--
        if (reverbValue < 0) {
            reverbValue = 0
        }
        reverbText.text = reverbValue.toString()
    }

    fun increaseReverb(view: View) {
        reverbValue++
        if (reverbValue > 10) {
            reverbValue = 10
        }
        reverbText.text = reverbValue.toString()
    }

    private fun updateDuration(currentPosition: Int, duration: Int) {
        val currentMinutes = (currentPosition / 1000) / 60
        val currentSeconds = (currentPosition / 1000) % 60
        val durationMinutes = (duration / 1000) / 60
        val durationSeconds = (duration / 1000) % 60

        val formattedCurrent = String.format("%02d:%02d", currentMinutes, currentSeconds)
        val formattedDuration = String.format("%02d:%02d", durationMinutes, durationSeconds)

        listenedDurationTextView.text = formattedCurrent
        songDurationTextView.text = formattedDuration
    }



}
