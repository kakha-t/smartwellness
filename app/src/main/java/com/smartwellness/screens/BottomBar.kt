package com.smartwellness.screens

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomBar(navController: NavController, userId: Int?) {
    val items = listOf(
        BottomNavItem("home", Icons.Default.Home, "Start"),
        BottomNavItem("nutrition", Icons.Default.Restaurant, "Ernährung"),
        BottomNavItem("fitness", Icons.Default.FitnessCenter, "Fitness"),
        BottomNavItem("zugang", Icons.Default.List, "Plan"),
        BottomNavItem("more", Icons.Default.MoreVert, "More")
    )

    var selectedRoute by remember { mutableStateOf("home") }

    NavigationBar(
        containerColor = Color(0xFFA3F18F),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute
            NavigationBarItem(
                selected = selected,
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
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) Color.Black else Color.DarkGray
                    )
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Color.Black else Color.DarkGray
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}