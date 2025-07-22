package com.smartwellness.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smartwellness.data.entities.Lebensmittel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.smartwellness.entities.Plan
import com.smartwellness.data.PlanRepository
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import com.smartwellness.firebase.FirestorePlanService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    navController: NavController,
    lebensmittelListe: List<Lebensmittel>,
    userEmail: String,
    userId: Int,
    planRepository: PlanRepository,
    onSavePlan: (Plan) -> Unit
) {
    var expandedSuchfeld by remember { mutableStateOf(false) }
    val ausgewaehlt = remember { mutableStateListOf<Pair<Lebensmittel, Float>>() }
    var selectedLebensmittel by remember { mutableStateOf<Lebensmittel?>(null) }
    var grammText by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("") }
    var expandedTag by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val grammFocusRequester = remember { FocusRequester() }
    var grammFehler by remember { mutableStateOf(false) }

    val tage = listOf(
        "Montag", "Dienstag", "Mittwoch", "Donnerstag",
        "Freitag", "Samstag", "Sonntag"
    )

    var filteredLebensmittelListe by remember { mutableStateOf(lebensmittelListe) }

    LaunchedEffect(searchQuery) {
        delay(300)

        filteredLebensmittelListe =
            if (searchQuery.isBlank()) {
                emptyList()
            } else if (searchQuery == "*" || searchQuery == " ") {
                lebensmittelListe.sortedBy { it.produkt.lowercase() }
            } else {
                lebensmittelListe
                    .filter {
                        it.produkt.contains(searchQuery, ignoreCase = true)
                    }
                    .sortedBy { it.produkt.lowercase() }
            }

        expandedSuchfeld = searchQuery.isNotEmpty()
    }

    val sumKalorien = ausgewaehlt
        .sumOf { (produkt, gramm) ->
            ((produkt.kalorien.toFloatOrNull() ?: 0f) * gramm / 100).toDouble()
        }
    val sumFett = ausgewaehlt
        .sumOf { (produkt, gramm) ->
            ((produkt.fett.toFloatOrNull() ?: 0f) * gramm / 100).toDouble()
        }
    val sumEiweiss = ausgewaehlt
        .sumOf { (produkt, gramm) ->
            ((produkt.eiweiss.toFloatOrNull() ?: 0f) * gramm / 100).toDouble()
        }
    val sumKohlenhydrate = ausgewaehlt
        .sumOf { (produkt, gramm) ->
            ((produkt.kohlenhydrate.toFloatOrNull() ?: 0f) * gramm / 100).toDouble()
        }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mein Plan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("plan/{userId}/{userEmail}") {
                                inclusive = true
                            }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Produkt auswählen", color = Color(0xFF2E7D32)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.clickable {
                                expandedSuchfeld = !expandedSuchfeld
                                if (expandedSuchfeld && searchQuery.isEmpty()) {
                                    coroutineScope.launch {
                                        focusRequester.requestFocus()
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF81C784),
                        unfocusedBorderColor = Color(0xFF81C784),
                        cursorColor = Color(0xFF2E7D32),
                        focusedLabelColor = Color(0xFF2E7D32)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (ausgewaehlt.isEmpty()) {
                    Text(
                        text = "Noch keine Produkte ausgewählt.",
                        color = Color(0xFFD32F2F),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (ausgewaehlt.isNotEmpty()) {
                    Text(
                        "Ausgewählte Produkte:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E7D32)
                    )

                    LazyColumn {
                        items(ausgewaehlt) { (produkt, gramm) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 0.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${produkt.produkt} - ${gramm} g - GI ${produkt.glyk_index}",
                                    color = Color.Black
                                )
                                IconButton(
                                    onClick = {
                                        ausgewaehlt.remove(produkt to gramm)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Entfernen",
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = Color(0xFF81C784),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 0.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Summierte Werte:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Kalorien: ${String.format("%.1f", sumKalorien)} kcal", color = Color.Black)
                            Text("Fett: ${String.format("%.1f", sumFett)} g", color = Color.Black)
                            Text("Eiweiß: ${String.format("%.1f", sumEiweiss)} g", color = Color.Black)
                            Text("Kohlenhydrate: ${String.format("%.1f", sumKohlenhydrate)} g", color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // === Dieser Block ist jetzt immer sichtbar ===
                Box {
                    OutlinedTextField(
                        value = selectedTag,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Tag wählen") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedTag = true },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Icon"
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = Color.Black,
                            disabledLabelColor = Color(0xFF2E7D32),
                            disabledBorderColor = Color(0xFF81C784),
                            focusedBorderColor = Color(0xFF81C784),
                            unfocusedBorderColor = Color(0xFF81C784)
                        )
                    )

                    DropdownMenu(
                        expanded = expandedTag,
                        onDismissRequest = { expandedTag = false }
                    ) {
                        tage.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag) },
                                onClick = {
                                    selectedTag = tag
                                    expandedTag = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (ausgewaehlt.isNotEmpty()) {
                            val jsonArray = ausgewaehlt.map { (produkt, gramm) ->
                                """
                {
                    "produkt":"${produkt.produkt}",
                    "menge":$gramm,
                    "kcal":"${produkt.kalorien}",
                    "fett":"${produkt.fett}",
                    "eiweiss":"${produkt.eiweiss}",
                    "kh":"${produkt.kohlenhydrate}",
                    "glyk_index":"${produkt.glyk_index}"
                }
                """.trimIndent()
                            }.joinToString(separator = ",", prefix = "[", postfix = "]")

                            val plan = Plan(
                                tag = selectedTag,
                                erstelltAm = System.currentTimeMillis().toString(),
                                aktualisiertAm = null,
                                datenJson = jsonArray,
                                userId = userId
                            )

                            coroutineScope.launch {
                                val existingPlan = planRepository.getPlanByUserAndTag(userId, selectedTag)

                                val planToSave = if (existingPlan != null) {
                                    plan.copy(
                                        id = existingPlan.id,
                                        aktualisiertAm = System.currentTimeMillis().toString()
                                    )
                                } else {
                                    plan
                                }

                                onSavePlan(planToSave)

                                val firestoreService = FirestorePlanService()
                                firestoreService.savePlanToFirebase(userEmail, planToSave)

                                ausgewaehlt.clear()
                                selectedTag = ""
                                searchQuery = ""

                                snackbarHostState.showSnackbar("Plan erfolgreich gespeichert!")

                            }

                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Bitte zuerst Produkte auswählen.")
                            }
                        }
                    },
                    enabled = selectedTag.isNotEmpty() && ausgewaehlt.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                ) {
                    Text("Plan speichern", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("saved_plans/$userId/$userEmail")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Text("Meine Tagespläne schauen", color = Color(0xFF2E7D32))
                }
            }

            if (expandedSuchfeld) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp)
                        .align(Alignment.TopCenter),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        if (filteredLebensmittelListe.isNotEmpty()) {
                            items(filteredLebensmittelListe) { produkt ->
                                ListItem(
                                    headlineContent = { Text(produkt.produkt) },
                                    modifier = Modifier
                                        .clickable {
                                            selectedLebensmittel = produkt
                                            searchQuery = ""
                                            expandedSuchfeld = false
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            item {
                                Text(
                                    "Keine Ergebnisse gefunden",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedLebensmittel?.let { produkt ->
        selectedLebensmittel?.let { produkt ->
            AlertDialog(
                onDismissRequest = { selectedLebensmittel = null },
                confirmButton = {
                    TextButton(onClick = {
                        val gramm = grammText.toFloatOrNull()
                        if (gramm != null && gramm > 0) {
                            ausgewaehlt.add(produkt to gramm)
                            selectedLebensmittel = null
                            grammText = ""
                            grammFehler = false // Reset Fehleranzeige
                        } else {
                            grammFehler = true // Zeige Fehlermeldung an
                        }
                    }) {
                        Text("Hinzufügen", color = Color(0xFF81C784))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectedLebensmittel = null
                        grammText = ""
                    }) {
                        Text("Abbrechen", color = Color.Red)
                    }
                },
                title = { Text(produkt.produkt) },
                text = {
                    OutlinedTextField(
                        value = grammText,
                        onValueChange = {
                            grammText = it
                            grammFehler = false // Zurücksetzen bei neuer Eingabe
                        },
                        label = { Text("Menge in Gramm") },
                        singleLine = true,
                        isError = grammFehler,
                        supportingText = {
                            if (grammFehler) {
                                Text("Bitte nur Zahlen eingeben", color = Color.Red)
                            }
                        },
                        modifier = Modifier.focusRequester(grammFocusRequester),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (grammFehler) Color.Red else Color(0xFF81C784),
                            unfocusedBorderColor = if (grammFehler) Color.Red else Color(0xFF81C784)
                        )
                    )
                }
            )

            // 🟢 Automatisch Fokus setzen
            LaunchedEffect(produkt) {
                grammFocusRequester.requestFocus()
            }
        }
    }
}