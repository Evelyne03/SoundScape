package com.example.soundscape

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.soundscape.loadCsv
import com.example.soundscape.Utils.songList


class LoginActivity : AppCompatActivity() {
    private lateinit var userEdt: EditText
    private lateinit var passEdt: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore


    private lateinit var passwordToggle: ImageView
    private var isPasswordVisible: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loadSongs()

        initView()

        setVariable()

        signup()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }
    }



    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            passEdt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            passEdt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passEdt.setSelection(passEdt.text.length)
        passwordToggle.setImageResource(if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye)
    }

    private fun loadSongs() {
        lifecycleScope.launch {
            songList = withContext(Dispatchers.IO) {
                loadCsv(this@LoginActivity)
            }
        }
    }


    private fun setVariable() {
        loginBtn.setOnClickListener {
            val email = userEdt.text.toString()
            val password = passEdt.text.toString()

            if (isValidEmail(email)) {
                userEdt.error = null
                if (password.isNotEmpty()) {
                    passEdt.error = null


                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                //update the password in document

                                firebase.collection("users").whereEqualTo("email", email).get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            val id = document.id
                                            val user = hashMapOf(
                                                "password" to password
                                            )
                                            firebase.collection("users").document(id).update(user as Map<String, Any>)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                                    }

                                startActivity(Intent(this@LoginActivity, ActionActivity::class.java))
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                finish()
                            } else {
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                                passEdt.requestFocus()
                            }
                        }


                    //startActivity(Intent(this@LoginActivity, ActionActivity::class.java))
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                   // finish()
                } else {
                    passEdt.error = "Password cannot be empty"
                    passEdt.requestFocus()
                }
            } else {
                userEdt.error = "Invalid email address"
                userEdt.requestFocus()
            }
        }

    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }



    private fun signup() {
        signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun initView() {
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()

        //testing

        //if(auth.currentUser != null){ auth.signOut() }

        if (auth.currentUser != null) {
            startActivity(Intent(this, ActionActivity::class.java))
           finish()
        }



        userEdt = findViewById(R.id.editTextTextPersonName)
        passEdt = findViewById(R.id.editTextTextPassword)
        loginBtn = findViewById(R.id.loginBtn)
        signupBtn = findViewById(R.id.SignUpBtn)
        passwordToggle = findViewById(R.id.passwordToggle)
    }

    fun forgotPassword(view: View) {
        // use firebase auth
        val email = userEdt.text.toString()
        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            userEdt.error = "Email cannot be empty"
            userEdt.requestFocus()
        }
    }


}
