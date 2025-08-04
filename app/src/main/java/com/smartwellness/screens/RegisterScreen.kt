package com.smartwellness.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.smartwellness.data.dao.UserDao
import com.smartwellness.data.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

// --- UI State ---
data class RegisterUiState(
    val vorname: String = "",
    val nachname: String = "",
    val email: String = "",
    val phone: String = "",
    val geburtstag: String = "",
    val password: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userDao: UserDao
) {
    val stateFlow = remember { MutableStateFlow(RegisterUiState()) }
    val state by stateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    val calendar = Calendar.getInstance()
    val (initialDay, initialMonth, initialYear) = state.geburtstag
        .split(".")
        .mapNotNull { it.toIntOrNull() }
        .let {
            Triple(
                it.getOrNull(0) ?: calendar.get(Calendar.DAY_OF_MONTH),
                (it.getOrNull(1)?.minus(1)) ?: calendar.get(Calendar.MONTH),
                it.getOrNull(2) ?: calendar.get(Calendar.YEAR)
            )
        }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formatted = String.format("%02d.%02d.%04d", dayOfMonth, month + 1, year)
            stateFlow.update { it.copy(geburtstag = formatted) }
        },
        initialYear,
        initialMonth,
        initialDay
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate("zugang") },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Zurück", tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Registrierung", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        state.successMessage?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Erfolg", tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text(it, color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Jetzt einloggen", color = Color.Blue, modifier = Modifier.clickable { navController.navigate("login") })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        state.errorMessage?.let {
            Text("❌ $it", color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(value = state.vorname, onValueChange = {
            stateFlow.update { s -> s.copy(vorname = it.filter { c -> c.isLetter() || c.isWhitespace() }) }
        }, label = { Text("Vorname") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = state.nachname, onValueChange = {
            stateFlow.update { s -> s.copy(nachname = it.filter { c -> c.isLetter() || c.isWhitespace() }) }
        }, label = { Text("Nachname") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = state.email, onValueChange = {
            stateFlow.update { s -> s.copy(email = it) }
        }, label = { Text("E-Mail") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = state.phone, onValueChange = {
            stateFlow.update { s -> s.copy(phone = it.filter { c -> c.isDigit() }) }
        }, label = { Text("Telefonnummer") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        OutlinedTextField(value = state.geburtstag, onValueChange = {
            val digits = it.filter { c -> c.isDigit() }.take(8)
            val formatted = buildString {
                digits.forEachIndexed { index, c ->
                    append(c)
                    if (index == 1 || index == 3) append(".")
                }
            }
            stateFlow.update { s -> s.copy(geburtstag = formatted) }
        }, label = { Text("Geburtsdatum (TT.MM.JJJJ)") }, modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Datum auswählen", tint = Color(0xFF4CAF50))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        OutlinedTextField(value = state.password, onValueChange = {
            stateFlow.update { s -> s.copy(password = it) }
        }, label = { Text("Passwort") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val datePattern = Regex("""\d{2}\.\d{2}\.\d{4}""")
                    if (state.vorname.isBlank() || state.nachname.isBlank() || state.email.isBlank() ||
                        state.phone.isBlank() || state.geburtstag.isBlank() || state.password.isBlank()) {
                        stateFlow.update { it.copy(errorMessage = "Bitte fülle alle Felder aus!", successMessage = null) }
                        return@launch
                    } else if (!datePattern.matches(state.geburtstag)) {
                        stateFlow.update { it.copy(errorMessage = "Bitte ein gültiges Datum im Format TT.MM.JJJJ eingeben!", successMessage = null) }
                        return@launch
                    }

                    auth.createUserWithEmailAndPassword(state.email.trim(), state.password.trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                coroutineScope.launch {
                                    try {
                                        val maxId = userDao.getMaxUserId() ?: 1000
                                        val nextId = maxId + 1
                                        val user = User(
                                            id = nextId,
                                            vorname = state.vorname.trim(),
                                            nachname = state.nachname.trim(),
                                            email = state.email.trim(),
                                            phone = state.phone.trim(),
                                            geburtstag = state.geburtstag.trim(),
                                            password = state.password.trim()
                                        )
                                        val userId = userDao.insertUser(user).toInt()

                                        val userMap = mapOf(
                                            "id" to userId,
                                            "vorname" to user.vorname,
                                            "nachname" to user.nachname,
                                            "email" to user.email,
                                            "phone" to user.phone,
                                            "geburtstag" to user.geburtstag,
                                            "password" to user.password,
                                            "timestamp" to System.currentTimeMillis()
                                        )

                                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(user.email)
                                            .set(userMap)

                                        stateFlow.update { it.copy(successMessage = "Registrierung erfolgreich!", errorMessage = null) }

                                    } catch (e: Exception) {
                                        Log.e("Register", "Fehler", e)
                                        stateFlow.update { it.copy(errorMessage = "Fehler beim Speichern des Benutzers.", successMessage = null) }
                                    }
                                }
                            } else {
                                val msg = task.exception?.message ?: "Fehler bei der Registrierung"
                                val error = when {
                                    msg.contains("least 6 characters", true) -> "Das Passwort muss mindestens 6 Zeichen lang sein!"
                                    msg.contains("badly formatted", true) -> "Ungültige E-Mail-Adresse!"
                                    msg.contains("already in use", true) -> "Diese E-Mail ist bereits registriert!"
                                    else -> "Registrierung fehlgeschlagen – bitte Eingaben prüfen."
                                }
                                Log.e("Register", "Firebase registration failed", task.exception)
                                stateFlow.update { it.copy(errorMessage = error, successMessage = null) }
                            }
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3F18F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Registrieren", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Schon registriert?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Hier anmelden",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }

    }

}


