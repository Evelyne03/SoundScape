package com.example.soundscape

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soundscape.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var nameEdt: EditText
    private lateinit var surnameEdt: EditText
    private lateinit var emailEdt: EditText
    private lateinit var passwordEdt: EditText
    private lateinit var rePasswordEdt: EditText
    private lateinit var signUpBtn: Button
    private lateinit var passwordToggle: ImageView
    private lateinit var rePasswordToggle: ImageView
    private lateinit var firebase: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        initView()
        setSignUpAction()
        setupPasswordToggle()
        //setupBackButton()
    }

    private fun initView() {
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        nameEdt = findViewById(R.id.PersonNameText)
        surnameEdt = findViewById(R.id.PersonSurnameText)
        emailEdt = findViewById(R.id.PersonEmailText)
        passwordEdt = findViewById(R.id.PasswordText)
        rePasswordEdt = findViewById(R.id.RePasswordText)
        signUpBtn = findViewById(R.id.SignUpBtn)
        passwordToggle = findViewById(R.id.passwordToggle)
        rePasswordToggle = findViewById(R.id.rePasswordToggle)
    }

    private fun setSignUpAction() {
        signUpBtn.setOnClickListener {
            val name = nameEdt.text.toString().trim()
            val surname = surnameEdt.text.toString().trim()
            val email = emailEdt.text.toString().trim()
            val password = passwordEdt.text.toString()
            val rePassword = rePasswordEdt.text.toString()

            val emailValid = isValidEmail(email)
            val passwordsValid = arePasswordsValid(password, rePassword)

            var allFieldsValid = true

            if (name.isEmpty()) {
                nameEdt.error = "Name is missing"
                allFieldsValid = false
            }
            if (surname.isEmpty()) {
                surnameEdt.error = "Surname is missing"
                allFieldsValid = false
            }
            if (email.isEmpty()) {
                emailEdt.error = "Email is missing"
                allFieldsValid = false
            } else if (!emailValid) {
                emailEdt.error = "Invalid email address"
                allFieldsValid = false
            }
            if (password.isEmpty()) {
                passwordEdt.error = "Password is missing"
                allFieldsValid = false
            }
            if (rePassword.isEmpty()) {
                rePasswordEdt.error = "Re-entered password is missing"
                allFieldsValid = false
            } else if (!passwordsValid) {
                rePasswordEdt.error = "Passwords do not match"
                allFieldsValid = false
            }

            if (allFieldsValid) {
                clearErrors()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = hashMapOf(
                                "name" to name,
                                "surname" to surname,
                                "email" to email,
                                "password" to password
                            )
                            firebase.collection("users").document(email).set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun setupPasswordToggle() {
        passwordToggle.setOnClickListener {
            togglePasswordVisibility(passwordEdt)
        }

        rePasswordToggle.setOnClickListener {
            togglePasswordVisibility(rePasswordEdt)
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        val isPasswordVisible = editText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        val inputType = if (isPasswordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.inputType = inputType
        editText.setSelection(editText.text.length)
        val toggleIcon = if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye
        if (editText == passwordEdt) {
            passwordToggle.setImageResource(toggleIcon)
        } else {
            rePasswordToggle.setImageResource(toggleIcon)
        }
    }

    private fun clearErrors() {
        nameEdt.error = null
        surnameEdt.error = null
        emailEdt.error = null
        passwordEdt.error = null
        rePasswordEdt.error = null
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun arePasswordsValid(password: String, rePassword: String): Boolean {
        return password == rePassword
    }

}
