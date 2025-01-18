package com.example.dineDelight.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController, selectedItem: String) {
    NavigationBar {
        listOf(
            NavigationItem("Home", Icons.Default.Home),
            NavigationItem("My Reservations", Icons.Default.DateRange),
            NavigationItem("My Reviews", Icons.Default.Edit),
            NavigationItem("Profile", Icons.Default.Person)
        ).forEach { item ->
            NavigationBarItem(
                selected = item.title == selectedItem,
                onClick = {
                    when (item.title) {
                        "Home" -> navController.navigate("home")
                        "My Reservations" -> navController.navigate("user_reservations")
                        "Profile" -> navController.navigate("profile")
                        "My Reviews" -> navController.navigate("my_reviews")
                    }
                },
                icon = { Icon(item.icon, item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

private data class NavigationItem(
    val title: String,
    val icon: ImageVector
)
