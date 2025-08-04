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

sealed class KontoUiState {
    data object Loading : KontoUiState()
    data class Success(val user: User) : KontoUiState()
    data class Error(val message: String) : KontoUiState()
}

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
    val uiState = remember { mutableStateOf<KontoUiState>(KontoUiState.Loading) }
    val isEditing = remember { mutableStateOf(false) }

    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val birthday = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        try {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                uiState.value = KontoUiState.Success(user)
                email.value = user.email
                phone.value = user.phone
                birthday.value = user.geburtstag
                password.value = user.password
            } else {
                uiState.value = KontoUiState.Error("Nutzer nicht gefunden.")
            }
        } catch (e: Exception) {
            uiState.value = KontoUiState.Error("Fehler beim Laden: ${e.localizedMessage}")
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
        when (val state = uiState.value) {
            is KontoUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }
            is KontoUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Fehler: ${state.message}", color = Color.Red)
                }
            }
            is KontoUiState.Success -> {
                val user = state.user
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Identität", fontWeight = FontWeight.Normal, color = Color(0xFF2E7D32), fontSize = 19.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow("Vorname:", user.vorname)
                    InfoRow("Nachname:", user.nachname)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Kontaktdaten", fontWeight = FontWeight.Normal, color = Color(0xFF2E7D32), fontSize = 19.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    EditableField("E-Mail:", email.value, { email.value = it }, isEditing.value)
                    EditableField("Telefon:", phone.value, {
                        phone.value = it.filter { ch -> ch.isDigit() }
                    }, isEditing.value)
                    EditableField("Geburtsdatum (TT.MM.JJJJ):", birthday.value, {
                        val digitsOnly = it.filter { c -> c.isDigit() }
                        val truncated = digitsOnly.take(8)
                        val formatted = buildString {
                            truncated.forEachIndexed { index, c ->
                                append(c)
                                if (index == 1 || index == 3) append(".")
                            }
                        }
                        birthday.value = formatted
                    }, isEditing.value)
                    EditableField("Passwort:", password.value, { password.value = it }, isEditing.value, isPassword = true)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing.value) {
                        Button(onClick = { isEditing.value = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.5f))) {
                            Text("Bearbeiten", color = Color.Black)
                        }
                    } else {
                        Button(onClick = {
                            coroutineScope.launch {
                                userRepository.updateUser(
                                    userId,
                                    email.value,
                                    phone.value,
                                    birthday.value,
                                    password.value
                                )
                            }
                            isEditing.value = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3F18F))) {
                            Text("Daten speichern", color = Color.Black)
                        }
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
        Text("Bitte logge dich ein, um deine Kontodaten zu sehen!", color = Color(0xFFD32F2F), fontWeight = FontWeight.Normal, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("login?returnTo=mein_konto")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3F18F))
        ) {
            Text("Hier einloggen", color = Color.Black)
        }
    }
}