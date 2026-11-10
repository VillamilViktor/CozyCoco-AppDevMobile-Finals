package com.example.cozycoco

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etBirthday: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etBirthday = findViewById(R.id.etBirthday)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        etBirthday.setOnClickListener { showDatePicker() }
        btnRegister.setOnClickListener { attemptRegister() }
        tvLogin.setOnClickListener {
            startActivity(Intent(this, ActivityLogin::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Already signed in? Skip registration.
        if (auth.currentUser != null) goToHome()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                etBirthday.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            },
            cal.get(Calendar.YEAR) - 18,
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun attemptRegister() {
        val email = etEmail.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val birthday = etBirthday.text.toString().trim()
        val password = etPassword.text.toString()
        val confirm = etConfirmPassword.text.toString()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                etEmail.error = "Enter a valid email"
            username.isEmpty() ->
                etUsername.error = "Enter a username"
            birthday.isEmpty() ->
                etBirthday.error = "Pick your birthday"
            password.length < 6 ->
                etPassword.error = "Password must be at least 6 characters"
            password != confirm ->
                etConfirmPassword.error = "Passwords don't match"
            else -> registerUser(email, password, username, birthday)
        }
    }

    private fun registerUser(email: String, password: String, username: String, birthday: String) {
        btnRegister.isEnabled = false

        // 1. Create the account in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage ?: "Registration failed",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnCompleteListener
                }

                val user = auth.currentUser ?: return@addOnCompleteListener

                // 2. Save the profile in Firestore (age is saved too)
                val userData = hashMapOf(
                    "username" to username,
                    "birthday" to birthday,
                    "age" to calculateAge(birthday),
                    "email" to email
                )

                db.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }
                    .addOnFailureListener { e ->
                        btnRegister.isEnabled = true
                        Toast.makeText(
                            this,
                            "Account created, but profile saving failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        goToHome()
                    }
            }
    }

    private fun calculateAge(birthday: String): Long {
        val p = birthday.split("-")
        val dob = Calendar.getInstance().apply {
            set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt())
        }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--
        return age.toLong()
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}