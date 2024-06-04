package com.example.soundscape

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var nameEdt: EditText
    private lateinit var surnameEdt: EditText
    private lateinit var emailEdt: EditText
    private lateinit var passwordEdt: EditText
    private lateinit var rePasswordEdt: EditText
    private lateinit var signUpBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        initView()
        setSignUpAction()
    }

    private fun initView() {
        nameEdt = findViewById(R.id.PersonNameText)
        surnameEdt = findViewById(R.id.PersonSurenameText)
        emailEdt = findViewById(R.id.PersonEmailText)
        passwordEdt = findViewById(R.id.PasswordText)
        rePasswordEdt = findViewById(R.id.RePasswordText)
        signUpBtn = findViewById(R.id.SignUpBtn)
    }

    private fun setSignUpAction() {
        signUpBtn.setOnClickListener {
            val email = emailEdt.text.toString().trim()
            val password = passwordEdt.text.toString()
            val rePassword = rePasswordEdt.text.toString()

            val emailValid = isValidEmail(email)
            val passwordsValid = arePasswordsValid(password, rePassword)

            if (emailValid && passwordsValid) {
                emailEdt.error = null  // Clear any previous email errors
                rePasswordEdt.error = null  // Clear any previous password errors
                // Add further signup logic here
            } else {
                // Show appropriate error messages
                if (!emailValid) {
                    emailEdt.error = "Invalid email address"
                    emailEdt.requestFocus()  // Show the error popup
                }
                if (!passwordsValid) {
                    rePasswordEdt.error = "Passwords do not match"
                    rePasswordEdt.requestFocus()  // Show the error popup
                }

            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun arePasswordsValid(password: String, rePassword: String): Boolean {
        return password == rePassword
    }
}
