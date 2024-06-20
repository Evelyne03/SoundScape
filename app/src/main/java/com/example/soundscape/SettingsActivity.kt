package com.example.soundscape

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Make the content appear behind the navigation bar
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        // Adjusts padding to ensure buttons and text are visible and not behind system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val barsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(barsInsets.left, barsInsets.top, barsInsets.right, barsInsets.bottom)
            insets
        }

        // Handle "Change Password" button click
        findViewById<Button>(R.id.change_password).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }
}
