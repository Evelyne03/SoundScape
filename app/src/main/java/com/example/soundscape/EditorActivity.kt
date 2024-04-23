package com.example.soundscape

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class EditorActivity : AppCompatActivity() {

    private var pitchValue: Int = 0
    private lateinit var lowPitch: Button
    private lateinit var highPitch: Button
    private lateinit var playButton: Button
    private lateinit var changesButton: Button
    private lateinit var fileSelector: Button
    private lateinit var uri: Uri
    private lateinit var soundText: TextView
    private lateinit var pitchText: TextView
    private var mediaPlayer: MediaPlayer? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private lateinit var newUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        lowPitch = findViewById(R.id.lowerPitch)
        highPitch = findViewById(R.id.increasePitch)
        playButton = findViewById(R.id.playButton)
        changesButton = findViewById(R.id.applyButton)
        fileSelector = findViewById(R.id.fileButton)
        soundText = findViewById(R.id.songNameTextView)
        pitchText = findViewById(R.id.pitchValue)
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
            uri = data?.data!! // Keep the URI in the uri variable

            uri?.let { selectedUri ->
                val cursor = contentResolver.query(selectedUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        // Get the column index of the MediaStore.Images.Media.DISPLAY_NAME
                        val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val fileName = it.getString(displayNameIndex)
                        soundText.text = fileName  // Set the file name to the TextView
                        Toast.makeText(this, "File selected: $fileName", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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

    fun playAudio(view: View) {
        if (!::newUri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, newUri)
            prepare()
            start()
        }
    }

    fun stopAudio(view: View) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
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

}
