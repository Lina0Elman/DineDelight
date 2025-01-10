//package com.example.dineDelight.activities
//
//import com.example.dineDelight.pages.LoginScreen
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.dineDelight.MainActivity
//import com.example.dineDelight.pages.RegisterScreen
//import com.google.firebase.auth.FirebaseAuth
//
//class LoginActivity : ComponentActivity() {
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance()
//
//        // auto login if user is authenticated
//        auth.currentUser?.let {
//            navigateToHome()
//        }
//
//        setContent {
//            val navController = rememberNavController()
//
//            NavHost(navController, startDestination = "login") {
//                composable("login") {
//                    LoginScreen(
//                        onLogin = { email, password ->
//                            auth.signInWithEmailAndPassword(email, password)
//                                .addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        navigateToHome()
//                                    } else {
//                                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        },
//                        onRegister = { email, password ->
//                            auth.createUserWithEmailAndPassword(email, password)
//                                .addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
//                                    } else {
//                                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        }
//                    )
//                }
//                composable("register") {
//                    RegisterScreen(navController)
//                }
//            }
//        }
//    }
//
//    private fun navigateToHome() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }
//}
