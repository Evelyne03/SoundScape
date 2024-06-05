package com.example.soundscape

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var nameEdt: EditText
    private lateinit var surnameEdt: EditText
    private lateinit var emailEdt: EditText
    private lateinit var passwordEdt: EditText
    private lateinit var rePasswordEdt: EditText
    private lateinit var signUpBtn: Button
    private lateinit var passwordToggle: ImageView
    private var isPasswordVisible: Boolean = false
    private lateinit var rePasswordToggle: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        initView()
        setSignUpAction()
        setupPasswordToggle()

    }

    private fun initView() {
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
            }
        }
    }

    private fun setupPasswordToggle() {
        passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        rePasswordToggle.setOnClickListener {
            toggleRePasswordVisibility()
        }
    }
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        val passwordInputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordEdt.inputType = passwordInputType
        passwordEdt.setSelection(passwordEdt.text.length)
        passwordToggle.setImageResource(if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye)
    }


    private fun toggleRePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        val passwordInputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        rePasswordEdt.inputType = passwordInputType
        rePasswordEdt.setSelection(rePasswordEdt.text.length)
        rePasswordToggle.setImageResource(if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye)
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
