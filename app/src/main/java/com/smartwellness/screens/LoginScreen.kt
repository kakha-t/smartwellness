package com.smartwellness.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartwellness.data.dao.UserDao
import com.smartwellness.data.entities.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.smartwellness.data.PlanRepository
import com.smartwellness.firebase.FirestorePlanService
import android.util.Log

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val userId: Int, val userEmail: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@Composable
fun LoginScreen(
    navController: NavController,
    userDao: UserDao,
    planRepository: PlanRepository,
    onLoginSuccess: (Int, String) -> Unit,
    returnTo: String?
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginUiState by remember { mutableStateOf<LoginUiState>(LoginUiState.Idle) }

    val coroutineScope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Zurück",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Login", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = loginUiState) {
            is LoginUiState.Error -> Text("❌ ${state.message}", color = Color.Red)
            is LoginUiState.Success -> Text("✅ Login erfolgreich!", color = Color(0xFF4CAF50))
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-Mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    if (email.isBlank() || password.isBlank()) {
                        loginUiState = LoginUiState.Error("Bitte fülle alle Felder aus!")
                        return@launch
                    }

                    loginUiState = LoginUiState.Loading

                    try {
                        val result = auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                        if (result.user == null) throw Exception("Kein Benutzer gefunden")

                        var user = userDao.getUserByEmail(email.trim())

                        if (user == null) {
                            val snapshot = FirebaseFirestore.getInstance()
                                .collection("users")
                                .whereEqualTo("email", email.trim())
                                .get()
                                .await()

                            if (!snapshot.isEmpty) {
                                val doc = snapshot.documents[0]
                                val userMap = doc.data ?: throw Exception("Daten unvollständig")

                                val firestoreUserId = (userMap["id"] as? Long)?.toInt() ?: 0
                                if (firestoreUserId == 0) Log.w("LoginScreen", "⚠️ Firestore-ID fehlt")

                                user = User(
                                    id = firestoreUserId,
                                    vorname = userMap["vorname"] as? String ?: "Unbekannt",
                                    nachname = userMap["nachname"] as? String ?: "Unbekannt",
                                    email = userMap["email"] as String,
                                    phone = userMap["phone"] as? String ?: "",
                                    geburtstag = userMap["geburtstag"] as? String ?: "",
                                    password = password.trim()
                                )

                                userDao.insertUser(user)
                                val plans = FirestorePlanService().loadPlansFromFirebase(user.id, user.email)
                                planRepository.insertOrReplacePlans(plans)
                                Log.d("LoginSync", "✅ ${plans.size} Pläne gespeichert")
                            } else {
                                throw Exception("Benutzerprofil nicht gefunden")
                            }
                        }

                        loginUiState = LoginUiState.Success(user.id, user.email)
                        onLoginSuccess(user.id, user.email)
                        navController.navigate(returnTo ?: "plan/${user.id}/${user.email}") {
                            popUpTo("login") { inclusive = true }
                        }
                    } catch (e: Exception) {
                        val msg = e.message ?: "Fehler beim Login"
                        loginUiState = LoginUiState.Error(
                            when {
                                msg.contains("password", true) -> "Passwort ist falsch!"
                                msg.contains("email", true) -> "E-Mail ist ungültig oder nicht registriert!"
                                msg.contains("auth credential", true) -> "Zugangsdaten sind ungültig oder abgelaufen!"
                                else -> "Login fehlgeschlagen: $msg"
                            }
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3F18F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Einloggen", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Noch kein Konto?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Jetzt registrieren",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )
        }
    }
}
