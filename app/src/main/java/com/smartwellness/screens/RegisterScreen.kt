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
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userDao: UserDao
) {
    var vorname by remember { mutableStateOf("") }
    var nachname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var geburtstag by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    val calendar = Calendar.getInstance()
    val (initialDay, initialMonth, initialYear) = geburtstag
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
            geburtstag = String.format("%02d.%02d.%04d", dayOfMonth, month + 1, year)
        },
        initialYear,
        initialMonth,
        initialDay
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("zugang") },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Zurück",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Registrierung", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        successMessage?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Erfolg",
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(it, color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Jetzt einloggen",
                    color = Color.Blue,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        errorMessage?.let {
            Text("❌ $it", color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(value = vorname, onValueChange = {
            vorname = it.filter { c -> c.isLetter() || c.isWhitespace() }
        }, label = { Text("Vorname") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = nachname, onValueChange = {
            nachname = it.filter { c -> c.isLetter() || c.isWhitespace() }
        }, label = { Text("Nachname") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("E-Mail") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = phone, onValueChange = {
            phone = it.filter { char -> char.isDigit() }
        }, label = { Text("Telefonnummer") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        OutlinedTextField(
            value = geburtstag,
            onValueChange = { newValue ->
                val digitsOnly = newValue.filter { it.isDigit() }
                val truncated = digitsOnly.take(8)
                val formatted = buildString {
                    truncated.forEachIndexed { index, c ->
                        append(c)
                        if (index == 1 || index == 3) append(".")
                    }
                }
                geburtstag = formatted
            },
            label = { Text("Geburtsdatum (TT.MM.JJJJ)") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.DateRange, "Datum auswählen", tint = Color(0xFF4CAF50))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Passwort") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val datePattern = Regex("""\d{2}\.\d{2}\.\d{4}""")
                    if (
                        vorname.isBlank() || nachname.isBlank() || email.isBlank() ||
                        phone.isBlank() || geburtstag.isBlank() || password.isBlank()
                    ) {
                        errorMessage = "Bitte fülle alle Felder aus!"
                        successMessage = null
                        return@launch
                    } else if (!datePattern.matches(geburtstag)) {
                        errorMessage = "Bitte ein gültiges Datum im Format TT.MM.JJJJ eingeben!"
                        successMessage = null
                        return@launch
                    }

                    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                coroutineScope.launch {
                                    try {
                                        val maxId = userDao.getMaxUserId() ?: 1000
                                        val nextId = maxId + 1
                                        val user = User(
                                            id = nextId,
                                            vorname = vorname.trim(),
                                            nachname = nachname.trim(),
                                            email = email.trim(),
                                            phone = phone.trim(),
                                            geburtstag = geburtstag.trim(),
                                            password = password.trim()
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
                                            .addOnSuccessListener {
                                                Log.d("Register", "✅ Firestore erfolgreich gespeichert")
                                            }
                                            .addOnFailureListener {
                                                Log.e("Register", "❌ Firestore Fehler", it)
                                            }

                                        successMessage = "Registrierung erfolgreich!"
                                        errorMessage = null

                                    } catch (e: Exception) {
                                        errorMessage = "Fehler beim Speichern des Benutzers."
                                        successMessage = null
                                        Log.e("Register", "❌ Fehler", e)
                                    }
                                }
                            } else {
                                val firebaseMessage = task.exception?.message ?: "Fehler bei der Registrierung"

                                errorMessage = when {
                                    firebaseMessage.contains("least 6 characters", ignoreCase = true) ->
                                        "Das Passwort muss mindestens 6 Zeichen lang sein!"
                                    firebaseMessage.contains("email address is badly formatted", ignoreCase = true) ->
                                        "Ungültige E-Mail-Adresse!"
                                    firebaseMessage.contains("already in use", ignoreCase = true) ->
                                        "Diese E-Mail ist bereits registriert!"
                                    else ->
                                        "Registrierung fehlgeschlagen – bitte Eingaben prüfen."
                                }

                                successMessage = null
                                Log.e("Register", "Firebase registration failed", task.exception)
                            }
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3F18F)),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Registrieren", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Schon registriert?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Hier anmelden",
                color = Color.Blue,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}