package com.smartwellness.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.saveable.rememberSaveable

// Datenklasse für ein Bottom Navigation Item
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomBar(
    navController: NavController,

) {
    val navItems = listOf(
        BottomNavItem("home", Icons.Default.Home, "Start"),
        BottomNavItem("nutrition", Icons.Default.Restaurant, "Ernährung"),
        BottomNavItem("fitness", Icons.Default.FitnessCenter, "Fitness"),
        BottomNavItem("zugang", Icons.Default.List, "Plan"),
        BottomNavItem("more", Icons.Default.MoreVert, "Mehr")
    )

    // StateFlow-ähnliche Navigation mit rememberSaveable
    var selectedRoute by rememberSaveable { mutableStateOf("home") }

    NavigationBar(
        containerColor = Color(0xFFA3F18F),
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            val isSelected = selectedRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    selectedRoute = item.route
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color.Black else Color.DarkGray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color.Black else Color.DarkGray
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
