package com.smartwellness.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartwellness.data.UserRepository
import com.smartwellness.data.entities.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeinKontoScreen(
    navController: NavController,
    userId: Int?,
    userRepository: UserRepository,
    onLogout: () -> Unit
) {
    if (userId == null) {
        NotLoggedInView(navController)
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        userRepository.getUserById(userId)?.let { loadedUser ->
            user = loadedUser
            email = loadedUser.email
            phone = loadedUser.phone ?: ""
            birthday = loadedUser.geburtstag ?: ""
            password = loadedUser.password ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mein Konto",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = {
                        onLogout()
                        navController.navigate("more") {
                            popUpTo("mein_konto") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    "Identität",
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF2E7D32),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                InfoRow("Vorname:", user!!.vorname)
                InfoRow("Nachname:", user!!.nachname)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Kontaktdaten",
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF2E7D32),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                EditableField(
                    label = "E-Mail:",
                    value = email,
                    onValueChange = { email = it },
                    enabled = isEditing
                )

                EditableField(
                    label = "Telefon:",
                    value = phone,
                    onValueChange = { newValue ->
                        phone = newValue.filter { it.isDigit() }
                    },
                    enabled = isEditing
                )

                EditableField(
                    label = "Geburtsdatum (TT.MM.JJJJ):",
                    value = birthday,
                    onValueChange = { newValue ->
                        val digitsOnly = newValue.filter { it.isDigit() }
                        val truncated = digitsOnly.take(8)
                        val formatted = buildString {
                            truncated.forEachIndexed { index, c ->
                                append(c)
                                if (index == 1 || index == 3) append(".")
                            }
                        }
                        birthday = formatted
                    },
                    enabled = isEditing
                )

                EditableField(
                    label = "Passwort:",
                    value = password,
                    onValueChange = { password = it },
                    enabled = isEditing,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isEditing) {
                    Button(
                        onClick = { isEditing = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Bearbeiten", color = Color.Black)
                    }
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                userRepository.updateUser(
                                    userId,
                                    email,
                                    phone,
                                    birthday,
                                    password
                                )
                            }
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA3F18F)
                        )
                    ) {
                        Text("Daten speichern", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value)
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF81C784),
            unfocusedBorderColor = Color(0xFF81C784),
            cursorColor = Color(0xFF2E7D32),
            focusedLabelColor = Color(0xFF2E7D32)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun NotLoggedInView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Bitte logge dich ein, um deine Kontodaten zu sehen!",
            color = Color(0xFFD32F2F),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("login?returnTo=mein_konto")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA3F18F)
            )
        ) {
            Text("Hier einloggen", color = Color.Black)
        }
    }
}