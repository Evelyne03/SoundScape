
package com.example.soundscape

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
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
import kotlin.coroutines.*



class EditorActivity : AppCompatActivity() {

    private var pitchValue: Int = 0
    private var speedValue: Float = 1.0f
    private var reverbValue: Int = 0
    private lateinit var lowPitch: Button
    private lateinit var highPitch: Button
    private lateinit var playButton: Button
    private lateinit var changesButton: Button
    private lateinit var fileSelector: Button
    private lateinit var uri: Uri
    private lateinit var soundText: TextView
    private lateinit var pitchText: TextView
    private lateinit var speedText: TextView
    private lateinit var reverbText: TextView
    private var mediaPlayer: MediaPlayer? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
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
        speedText = findViewById(R.id.speedValue)
        reverbText = findViewById(R.id.reverbValue)
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
            soundText.text = uri.toString()
            newUri = Uri.EMPTY // Reset newUri when a new file is selected
            Toast.makeText(this, "File selected: $uri", Toast.LENGTH_SHORT).show()
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


    fun applyChanges(view: View) {
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


    fun playAudio(view: View) {
        if (!::newUri.isInitialized || newUri == Uri.EMPTY) {
            if (!::uri.isInitialized) {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
                return
            } else {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(applicationContext, uri)
                    prepare()
                    start()
                }
                return
            }
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

    fun denoiseAudio(view: View) {
        if (!::uri.isInitialized) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            return
        }

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
                runOnUiThread { Toast.makeText(applicationContext, "Error: Failed to connect to server", Toast.LENGTH_LONG).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val outputFile = File(getExternalFilesDir(null), "denoised_output.wav")
                        outputFile.outputStream().use { fileOut ->
                            response.body?.byteStream()?.copyTo(fileOut)
                        }
                        newUri = Uri.fromFile(outputFile)
                        runOnUiThread { Toast.makeText(applicationContext, "Audio denoised and saved", Toast.LENGTH_SHORT).show() }
                    } else {
                        runOnUiThread { Toast.makeText(applicationContext, "Server error: ${response.code}", Toast.LENGTH_LONG).show() }
                    }
                }
            }
        })
    }


    fun denoiseAndModifyPitch(view: View) {
        denoiseAudio(view)
        modifyPitch(view)
    }


}