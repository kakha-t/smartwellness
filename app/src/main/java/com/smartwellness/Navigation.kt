package com.smartwellness

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartwellness.data.AppDatabase
import com.smartwellness.data.PlanRepository
import com.smartwellness.data.entities.Lebensmittel
import com.smartwellness.data.UserRepository
import com.smartwellness.screens.*
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    db: AppDatabase,
    lebensmittelListe: List<Lebensmittel>
) {
    val navController = rememberNavController()
    val planRepository = PlanRepository(db.planDao())
    val coroutineScope = rememberCoroutineScope()

    var userId by remember { mutableStateOf<Int?>(null) }
    var userEmail by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = { BottomBar(navController, userId) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "home") {
                HomeScreen(navController)
            }
            composable(route = "nutrition") {
                NutritionScreen(navController)
            }
            composable(route = "fitness") {
                FitnessScreen(navController)
            }
            composable(route = "zugang") {
                ZugangScreen(navController)
            }
            composable(route = "register") {
                RegisterScreen(navController, userDao = db.userDao())
            }
            composable(route = "datenschutz") {
                DatenschutzScreen(navController = navController)
            }
            composable(route = "impressum") {
                ImpressumScreen(navController = navController)
            }
            composable("more") {
                MoreScreen(
                    navController = navController,
                    userId = userId
                )
            }
            composable("mein_konto") {
                MeinKontoScreen(
                    navController = navController,
                    userId = userId,
                    userRepository = UserRepository(db.userDao()),
                    onLogout = {
                        userId = null
                        userEmail = null
                    }
                )
            }
            composable(
                route = "login?returnTo={returnTo}",
                arguments = listOf(
                    navArgument("returnTo") {
                        nullable = true
                        defaultValue = null
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val returnTo = backStackEntry.arguments?.getString("returnTo")
                LoginScreen(
                    navController = navController,
                    userDao = db.userDao(),
                    planRepository = planRepository,
                    returnTo = returnTo,
                    onLoginSuccess = { id, email ->
                        userId = id
                        userEmail = email
                        if (returnTo != null) {
                            navController.navigate(returnTo) {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("plan/$id/$email") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(
                route = "plan/{userId}/{userEmail}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType },
                    navArgument("userEmail") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val passedUserId = backStackEntry.arguments?.getInt("userId") ?: -1
                val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""

                PlanScreen(
                    navController = navController,
                    lebensmittelListe = lebensmittelListe,
                    userEmail = userEmail,
                    userId = passedUserId,
                    planRepository = planRepository,
                    onSavePlan = { plan ->
                        coroutineScope.launch {
                            planRepository.insertOrReplacePlan(plan)
                            println("✅ Plan gespeichert!")
                        }
                    }
                )
            }
            composable(
                route = "saved_plans/{userId}/{userEmail}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType },
                    navArgument("userEmail") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""

                SavedPlansScreen(
                    navController = navController,
                    userId = userId,
                    userEmail = userEmail,
                    planRepository = planRepository
                )
            }
        }
    }
}