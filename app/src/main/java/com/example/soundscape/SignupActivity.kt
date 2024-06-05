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
            }
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
