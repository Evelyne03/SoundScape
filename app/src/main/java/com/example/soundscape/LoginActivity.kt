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


class LoginActivity : AppCompatActivity() {
    private lateinit var userEdt: EditText
    private lateinit var passEdt: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button


    private lateinit var passwordToggle: ImageView
    private var isPasswordVisible: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

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


    private fun setVariable() {
        loginBtn.setOnClickListener {
            val email = userEdt.text.toString()
            val password = passEdt.text.toString()

            if (isValidEmail(email)) {
                userEdt.error = null
                if (password.isNotEmpty()) {
                    passEdt.error = null
                    startActivity(Intent(this@LoginActivity, ActionActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
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
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun initView() {
        userEdt = findViewById(R.id.editTextTextPersonName)
        passEdt = findViewById(R.id.editTextTextPassword)
        loginBtn = findViewById(R.id.loginBtn)
        signupBtn = findViewById(R.id.SignUpBtn)
        passwordToggle = findViewById(R.id.passwordToggle)
    }


}
