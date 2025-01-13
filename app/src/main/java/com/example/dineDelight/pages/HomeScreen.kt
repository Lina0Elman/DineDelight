package com.example.dineDelight.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(firebaseAuth.currentUser) }

    // Firebase Authentication state listener
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser == null) {
                // Navigate back to login when user signs out
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        onDispose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    val bottomBarItems = listOf("Home", "Reservations", "Profile")

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("DineDelight") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomBarItems.forEach { item ->
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            when (item) {
                                "Home" -> navController.navigate("home")
                                "Reservations" -> navController.navigate("reservations")
                                "Profile" -> navController.navigate("profile")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    "Home" -> Icons.Default.Home
                                    "Reservations" -> Icons.Default.DateRange
                                    "Profile" -> Icons.Default.Person
                                    else -> Icons.Default.Home
                                },
                                contentDescription = item,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(item) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentUser != null) {
                    // Show a personalized welcome message if the user is logged in
                    Text(
                        text = "Hello, ${currentUser?.email ?: "User"}!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { firebaseAuth.signOut() }) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}
