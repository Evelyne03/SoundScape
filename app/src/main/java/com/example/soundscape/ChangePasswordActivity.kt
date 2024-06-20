package com.example.soundscape

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmNewPassword: EditText
    private lateinit var submitButton: Button
    private lateinit var firebase: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password_activity)

        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
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



        oldPassword = findViewById<EditText>(R.id.old_password)
        newPassword = findViewById<EditText>(R.id.new_password)
        confirmNewPassword = findViewById<EditText>(R.id.confirm_new_password)
        submitButton = findViewById<Button>(R.id.submit_button)

        submitButton.setOnClickListener {
            val oldPass = oldPassword.text.toString()
            val newPass = newPassword.text.toString()
            val confirmPass = confirmNewPassword.text.toString()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = auth.currentUser?.email
            if (email != null) {
                val user = firebase.collection("users").whereEqualTo("email", email)

                user.get().addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val dbPassword = documents.documents[0].getString("password")
                    if (dbPassword != oldPass) {
                        Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    if (newPass.length < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    if (newPass == confirmPass) {
                        // Re-authenticate the user
                        val credential = EmailAuthProvider.getCredential(email, oldPass)
                        auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                // Re-authentication succeeded, now change the password
                                auth.currentUser!!.updatePassword(newPass)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            // Start the activity you want to keep open and clear all others
                                            val intent = Intent(this, LoginActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            //finish()  // Optionally finish the current activity
                                        } else {
                                            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Re-authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to retrieve user", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }



    }
}
