package com.example.dineDelight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            var errorMessage by remember { mutableStateOf<String?>(null) }

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
                                        errorMessage = task.exception?.message
                                    }
                                }
                        },
                        errorMessage
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
