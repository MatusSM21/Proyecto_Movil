package com.example.finansmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val isRegistered = databaseHelper.registerUser(username, email, password)
                if (isRegistered) {
                    Toast.makeText(this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar. Intenta con un usuario o correo diferente.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
