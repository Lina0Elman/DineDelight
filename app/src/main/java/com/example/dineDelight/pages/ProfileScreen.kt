package com.example.dineDelight.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dineDelight.R
import com.example.dineDelight.models.Image
import com.example.dineDelight.repositories.ImageRepository
import com.example.dineDelight.utils.BlobUtils.toBase64String
import com.example.dineDelight.utils.BlobUtils.toBitmap
import com.example.dineDelight.utils.BlobUtils.toBlob
import com.example.dineDelight.utils.BlobUtils.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val user = firebaseAuth.currentUser
    val userId = user?.uid.orEmpty()
    var userName by rememberSaveable { mutableStateOf(user?.displayName.orEmpty()) }
    val userEmail by rememberSaveable { mutableStateOf(user?.email.orEmpty()) }

    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            isLoading = true
            coroutineScope.launch {
                try {
                    val imageDoc = ImageRepository.getImageById(userId)
                    val base64String = imageDoc?.blobBase64String
                    if (!base64String.isNullOrEmpty()) {
                        val bitmap = base64String.toBlob()?.toBitmap()
                        profileImageUri = bitmap?.toUri(context)
                    }
                } finally {
                    isLoading = false
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            profileImageUri = selectedUri
            coroutineScope.launch {
                try {
                    val blob = selectedUri.toBlob(context)
                    val base64String = blob?.toBase64String()
                    if (!base64String.isNullOrEmpty()) {
                        ImageRepository.addImage(Image(
                            id = userId,
                            blobBase64String = base64String
                        ))

                        val updates = userProfileChangeRequest {
                            photoUri = selectedUri
                        }
                        user?.updateProfile(updates)
                    }
                } catch (e: IllegalArgumentException) {
                    errorMessage = e.message
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = profileImageUri ?: R.drawable.default_profile_image,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Change Profile Picture")
                }
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            val updates = userProfileChangeRequest {
                                displayName = userName.trim()
                            }
                            user?.updateProfile(updates)
                        } catch (e: Exception) {
                            errorMessage = e.message
                        }
                    }
                }) {
                    Text("Update Name")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Email: $userEmail", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    firebaseAuth.signOut()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }) {
                    Text("Logout")
                }
            }
        }
    }
}