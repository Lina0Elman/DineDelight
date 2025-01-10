package com.example.dineDelight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dineDelight.pages.HomeScreen
import com.example.dineDelight.pages.LoginScreen
import com.example.dineDelight.pages.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        auth = FirebaseAuth.getInstance()

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        navController = navController,
                        onLogin = { email, password ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("Login", "Login successful!")
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        Log.d("Login Error", task.exception?.message ?: "Unknown error")
                                    }
                                }
                        }
                    )
                }
                composable("register") {
                    RegisterScreen(navController)
                }
                composable("home") {
                    HomeScreen(navController)
                }
            }
        }
    }
}
