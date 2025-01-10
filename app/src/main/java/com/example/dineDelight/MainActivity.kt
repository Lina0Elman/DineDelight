package com.example.dineDelight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                    // Pass onLogin and onRegister lambdas to LoginScreen
                    LoginScreen(
                        navController = navController,
                        onLogin = { email, password ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        // Handle login error (e.g., show a Toast)
                                    }
                                }
                        },
                        onRegister = { email, password ->
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Handle successful registration (e.g., navigate to home or show success message)
                                    } else {
                                        // Handle registration error (e.g., show a Toast)
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
