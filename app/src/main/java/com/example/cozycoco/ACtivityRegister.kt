package com.example.cozycoco//package com.example.cozycoco
//
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.auth
//
//class ActivityRegister : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_register)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        auth = Firebase.auth
//
//        // Replace these IDs with the ones in your layout
//        findViewById<Button>(R.id.btnRegister).setOnClickListener {
//            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
//            val password = findViewById<EditText>(R.id.etPassword).text.toString()
//
//            if (email.isEmpty() || password.length < 6) {
//                Toast.makeText(this, "Enter an email and a password of 6+ characters.", Toast.LENGTH_SHORT).show()
//            } else {
//                registerUser(email, password)
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        // If already signed in, skip registration
//        updateUI(auth.currentUser)
//    }
//
//    private fun registerUser(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "createUserWithEmail:success")
//                    updateUI(auth.currentUser)
//                } else {
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//            }
//    }
//
//    private fun updateUI(user: FirebaseUser?) {
//        if (user != null) {
//            // e.g. startActivity(Intent(this, MainActivity::class.java)); finish()
//        }
//    }
//
//    companion object {
//        private const val TAG = "ActivityRegister"
//    }
//}