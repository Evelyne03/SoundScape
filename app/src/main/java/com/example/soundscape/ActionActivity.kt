package com.example.soundscape

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.action_activity)

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the custom back button
        findViewById<Button>(R.id.button).setOnClickListener {
            goToLoginActivity()
        }

        // Handle "Audio processing" button click
        findViewById<Button>(R.id.audio_processing).setOnClickListener {
            startActivity(Intent(this@ActionActivity, EditorActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Handle the back button click in the action bar
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                goToLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Override the onBackPressed method to provide custom transition animations
    override fun onBackPressed() {
        goToLoginActivity()
    }

    // Method to navigate to LoginActivity with transition
    private fun goToLoginActivity() {
        val intent = Intent(this@ActionActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()  // Optionally finish ActionActivity to remove it from the back stack
    }
}
