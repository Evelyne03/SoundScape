package com.example.soundscape

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ActionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.action_activity)
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.button3).setOnClickListener {
            favoriteSongs()
        }


        // Make the content appear behind the navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the logout button
        findViewById<Button>(R.id.button).setOnClickListener {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
            goToLoginActivity()
        }

        // Handle "Audio processing" button click
        findViewById<Button>(R.id.audio_processing).setOnClickListener {
            startActivity(Intent(this@ActionActivity, EditorActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Handle "Recommendation" button click
        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this@ActionActivity, RecommenderActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Handle "Settings" button click
        findViewById<Button>(R.id.button_settings).setOnClickListener {
            startActivity(Intent(this@ActionActivity, SettingsActivity::class.java))
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
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
                goToLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Override the onBackPressed method to provide custom transition animations
    override fun onBackPressed() {
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
        goToLoginActivity()
    }

    // Method to navigate to LoginActivity with transition
    private fun goToLoginActivity() {
        auth.signOut()
        val intent = Intent(this@ActionActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()  // Optionally finish ActionActivity to remove it from the back stack
    }

    private fun favoriteSongs() {
        startActivity(Intent(this@ActionActivity, FavoriteSongsActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
