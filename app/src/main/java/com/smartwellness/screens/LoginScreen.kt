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

@OptIn(ExperimentalMaterial3Api::class)
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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Zurück-Button
        Row(
            modifier = Modifier.fillMaxWidth().clickable { navController.popBackStack() },
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

        successMessage?.let {
            Text("✅ $it", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
        }

        errorMessage?.let {
            Text("❌ $it", color = Color.Red, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                        errorMessage = "Bitte fülle alle Felder aus!"
                        successMessage = null
                        return@launch
                    }

                    auth.signInWithEmailAndPassword(email.trim(), password.trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                coroutineScope.launch {
                                    try {
                                        var user = userDao.getUserByEmail(email.trim())

                                        // Falls Benutzer lokal nicht vorhanden ist – versuche aus Firestore zu laden
                                        if (user == null) {
                                            val snapshot = FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .whereEqualTo("email", email.trim())
                                                .get()
                                                .await()

                                            if (!snapshot.isEmpty) {
                                                val doc = snapshot.documents[0]
                                                val userMap = doc.data

                                                if (userMap == null || userMap["email"] == null) {
                                                    errorMessage = "Benutzerprofil unvollständig oder fehlerhaft. Bitte registrieren."
                                                    successMessage = null
                                                    return@launch
                                                }

                                                val firestoreUserId = (userMap["id"] as? Long)?.toInt() ?: 0

                                                if (firestoreUserId == 0) {
                                                    Log.w("LoginScreen", "⚠️ Firestore-ID fehlt oder ist 0 – mögliche Dateninkonsistenz")
                                                }

                                                user = User(
                                                    id = firestoreUserId, // ✅ Feste ID aus Firestore
                                                    vorname = userMap["vorname"] as? String ?: "Unbekannt",
                                                    nachname = userMap["nachname"] as? String ?: "Unbekannt",
                                                    email = userMap["email"] as String,
                                                    phone = userMap["phone"] as? String ?: "",
                                                    geburtstag = userMap["geburtstag"] as? String ?: "",
                                                    password = password.trim()
                                                )

                                                userDao.insertUser(user) // ID ist schon festgelegt – Room übernimmt diese direkt
                                                val planService = FirestorePlanService()
                                                val plans = planService.loadPlansFromFirebase(user.id, user.email)
                                                planRepository.insertOrReplacePlans(plans)
                                                Log.d("LoginSync", "✅ ${plans.size} Pläne aus Firestore in Room gespeichert")
                                            } else {
                                                errorMessage = "Benutzerprofil konnte nicht gefunden werden. Bitte registrieren."
                                                successMessage = null
                                                return@launch
                                            }
                                        }

                                        successMessage = "Login erfolgreich!"
                                        errorMessage = null
                                        onLoginSuccess(user.id, user.email)
                                        navController.navigate(returnTo ?: "plan/${user.id}/${user.email}") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Fehler beim Laden des Benutzerprofils: ${e.localizedMessage}"
                                        successMessage = null
                                    }
                                }
                            } else {
                                val message = task.exception?.message ?: "Fehler beim Login"
                                errorMessage = when {
                                    message.contains("password", ignoreCase = true) -> "Passwort ist falsch!"
                                    message.contains("email", ignoreCase = true) -> "E-Mail ist ungültig oder nicht registriert!"
                                    else -> "Login fehlgeschlagen – bitte Daten prüfen."
                                }
                                successMessage = null
                            }
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