package com.example.dineDelight.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dineDelight.R
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
    var showSignInDialog by remember { mutableStateOf(false) } // State to control dialog visibility

    // Firebase Authentication state listener
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
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
                        text = "Welcome back, ${currentUser?.email ?: "User"}!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { firebaseAuth.signOut() }) {
                        Text("Sign Out")
                    }
                } else {
                    // Prompt user to sign in if not authenticated
                    Text(
                        text = "Please sign in to enjoy all features!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showSignInDialog = true }) {
                        Text("Sign In")
                    }
                }
            }
        }
        if (showSignInDialog) {
            SignInDialog(
                onDismiss = { showSignInDialog = false },
                onSignIn = { email, password ->
                    handleSignIn(firebaseAuth, email, password) {
                        showSignInDialog = false // Close dialog after successful sign-in
                    }
                }
            )
        }
    }
}

@Composable
fun SignInDialog(
    onDismiss: () -> Unit,
    onSignIn: (email: String, password: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign In") },
        text = {
            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    onSignIn(email, password)
                } else {
                    errorMessage = "Please fill in all fields"
                }
            }) {
                Text("Sign In")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun handleSignIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: () -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseAuth", "Sign-in successful: ${auth.currentUser?.email}")
                onSignInSuccess()
            } else {
                Log.e("FirebaseAuth", "Sign-in failed: ${task.exception?.message}")
            }
        }
}
