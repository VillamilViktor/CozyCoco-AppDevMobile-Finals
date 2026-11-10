package com.example.cozycoco

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
//        val tvEmail = findViewById<TextView>(R.id.tvEmail)
//        val tvBirthday = findViewById<TextView>(R.id.tvBirthday)
//        val tvAge = findViewById<TextView>(R.id.tvAge)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser
        if (user == null) {
            goToLogin()
            return
        }

//        tvEmail.text = "Email: ${user.email}"

        // Load the profile saved during registration
        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: ""
                    val birthday = doc.getString("birthday") ?: ""
                    val age = doc.getLong("age") ?: 0L

                    tvWelcome.text = "Welcome to Cozy Coco, $username!"
//                    tvBirthday.text = "Birthday: $birthday"
//                    tvAge.text = "Age: $age"
                } else {
                    Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to retrieve profile: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}