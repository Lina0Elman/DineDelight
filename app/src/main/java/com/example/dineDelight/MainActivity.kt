package com.example.dineDelight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dineDelight.pages.HomeScreen
import com.example.dineDelight.pages.LoginScreen
import com.example.dineDelight.pages.RegisterScreen
import com.example.dineDelight.pages.RestaurantDetailsScreen
import com.example.dineDelight.pages.ReservationScreen
import com.example.dineDelight.pages.UpdateReservationScreen
import com.example.dineDelight.pages.UserReservationsScreen
import com.example.dineDelight.repositories.RestaurantRepository
import com.example.dineDelight.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth
import com.example.dineDelight.pages.UserReviewsScreen


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    val restaurants = RestaurantRepository.getRestaurants()

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

                        composable("my_reviews") {
                            UserReviewsScreen(navController)
                        }

                        composable("user_reservations") {
                            UserReservationsScreen(navController)
                        }

                        composable("update_reservation/{reservationId}") { backStackEntry ->
                            val reservationId = UUID.fromString(backStackEntry.arguments?.getString("reservationId"))
                            UpdateReservationScreen(navController, reservationId)
                        }

                        // Set up navigation for each restaurant
                        restaurants.forEach { restaurant ->
                            composable("restaurant/${restaurant.id}") { 
                                RestaurantDetailsScreen(navController, restaurant) 
                            }

                            composable("reserve/{restaurantId}") { backStackEntry ->
                                val restaurantId = backStackEntry.arguments?.getString("restaurantId")
                                val restaurant = RestaurantRepository.getRestaurants().find { it.id.toString() == restaurantId }
                                restaurant?.let { ReservationScreen(navController, it) }
                            }
                        }
                    }
                }
            }
        }
    }
}