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
import com.smartwellness.data.UserRepository
import com.smartwellness.data.entities.Lebensmittel
import com.smartwellness.screens.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun AppNavigation(
    db: AppDatabase,
    lebensmittelListe: List<Lebensmittel>
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val planRepository = remember { PlanRepository(db.planDao()) }
    val userRepository = remember { UserRepository(db.userDao()) }

    var userId by rememberSaveable { mutableStateOf<Int?>(null) }
    var userEmail by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(navController)
            }

            composable("nutrition") {
                NutritionScreen(navController)
            }

            composable("fitness") {
                FitnessScreen(navController)
            }

            composable("zugang") {
                ZugangScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController, db.userDao())
            }

            composable("datenschutz") {
                DatenschutzScreen(navController)
            }

            composable("impressum") {
                ImpressumScreen(navController)
            }

            composable("more") {
                MoreScreen(navController, userId)
            }

            composable("mein_konto") {
                MeinKontoScreen(
                    navController = navController,
                    userId = userId,
                    userRepository = userRepository,
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
                        val destination = returnTo ?: "plan/$id/$email"
                        navController.navigate(destination) {
                            popUpTo("login") { inclusive = true }
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
                val passedUserEmail = backStackEntry.arguments?.getString("userEmail") ?: ""

                PlanScreen(
                    navController = navController,
                    lebensmittelListe = lebensmittelListe,
                    userEmail = passedUserEmail,
                    userId = passedUserId,
                    planRepository = planRepository,
                    onSavePlan = { plan ->
                        coroutineScope.launch {
                            planRepository.insertOrReplacePlan(plan)
                            println("✅ Plan gespeichert")
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
                val passedUserId = backStackEntry.arguments?.getInt("userId") ?: -1
                val passedUserEmail = backStackEntry.arguments?.getString("userEmail") ?: ""

                SavedPlansScreen(
                    navController = navController,
                    userId = passedUserId,
                    userEmail = passedUserEmail,
                    planRepository = planRepository
                )
            }
        }
    }
}