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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartwellness.data.PlanRepository
import com.smartwellness.data.entities.Lebensmittel
import com.smartwellness.entities.Plan
import com.smartwellness.firebase.FirestorePlanService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.ui.platform.testTag



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
    val grammFocusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var grammFehler by remember { mutableStateOf(false) }

    val tage = listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
    var filteredLebensmittelListe by remember { mutableStateOf(lebensmittelListe) }

    LaunchedEffect(searchQuery) {
        delay(300)
        filteredLebensmittelListe = when {
            searchQuery.isBlank() -> emptyList()
            searchQuery == "*" || searchQuery == " " -> lebensmittelListe.sortedBy { it.produkt.lowercase() }
            else -> lebensmittelListe.filter {
                it.produkt.contains(searchQuery, ignoreCase = true)
            }.sortedBy { it.produkt.lowercase() }
        }
        expandedSuchfeld = searchQuery.isNotEmpty()
    }

    val sumKalorien = ausgewaehlt.sumOf { (p, g) -> ((p.kalorien.toFloatOrNull() ?: 0f) * g / 100).toDouble() }
    val sumFett = ausgewaehlt.sumOf { (p, g) -> ((p.fett.toFloatOrNull() ?: 0f) * g / 100).toDouble() }
    val sumEiweiss = ausgewaehlt.sumOf { (p, g) -> ((p.eiweiss.toFloatOrNull() ?: 0f) * g / 100).toDouble() }
    val sumKohlenhydrate = ausgewaehlt.sumOf { (p, g) -> ((p.kohlenhydrate.toFloatOrNull() ?: 0f) * g / 100).toDouble() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Mein Plan", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("plan/{userId}/{userEmail}") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFF2E7D32))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {

            if (ausgewaehlt.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp)) // sorgt für Abstand
                Text(
                    text = "Noch keine Produkte ausgewählt.",
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp) // zusätzlicher Abstand
                )
            } else {
                Text("Ausgewählte Produkte:", color = Color(0xFF2E7D32))
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .testTag("planList")      // für AndroidTest
                ) {
                    items(ausgewaehlt) { (p, g) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${p.produkt} - ${"%.1f".format(g)} g - GI ${p.glyk_index}",
                                modifier = Modifier.weight(1f),
                                color = Color.Black
                            )
                            IconButton(
                                onClick = { ausgewaehlt.remove(p to g) },
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
                            thickness = 0.8.dp
                        )
                    }
                }
            }

            if (ausgewaehlt.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Summierte Werte:", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("Kalorien: ${"%.1f".format(sumKalorien)} kcal")
                        Text("Fett: ${"%.1f".format(sumFett)} g")
                        Text("Eiweiß: ${"%.1f".format(sumEiweiss)} g")
                        Text("Kohlenhydrate: ${"%.1f".format(sumKohlenhydrate)} g")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box {
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

                if (expandedSuchfeld && filteredLebensmittelListe.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 56.dp), // Direkt unterhalb des Textfelds
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
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
                        }
                    }
                }
            }

            Box {
                OutlinedTextField(
                    value = selectedTag,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Tag wählen", color = Color(0xFF2E7D32)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedTag = true },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
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
                    onDismissRequest = { expandedTag = false },
                    modifier = Modifier
                        .width(IntrinsicSize.Min) // ⚠️ Schmaler wie in Screenshot 1
                ) {
                    tage.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTag = tag
                                    expandedTag = false
                                }
                                .background(
                                    if (selectedTag == tag) Color(0xFF2E7D32).copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp) // 👉 Hier wirkt es wirklich!
                        ) {
                            Text(
                                text = tag,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (ausgewaehlt.isNotEmpty()) {
                        val json = ausgewaehlt.joinToString(",", "[", "]") { (p, g) ->
                            """{"produkt":"${p.produkt}","menge":$g,"kcal":"${p.kalorien}","fett":"${p.fett}","eiweiss":"${p.eiweiss}","kh":"${p.kohlenhydrate}","glyk_index":"${p.glyk_index}"}"""
                        }
                        val now = System.currentTimeMillis().toString()
                        coroutineScope.launch {
                            val existing = planRepository.getPlanByUserAndTag(userId, selectedTag)
                            val plan = Plan(
                                id = existing?.id ?: 0,
                                tag = selectedTag,
                                erstelltAm = now,
                                aktualisiertAm = if (existing != null) now else null,
                                datenJson = json,
                                userId = userId
                            )
                            onSavePlan(plan)
                            FirestorePlanService().savePlanToFirebase(userEmail, plan)
                            ausgewaehlt.clear(); selectedTag = ""; searchQuery = ""
                            snackbarHostState.showSnackbar("Plan erfolgreich gespeichert!")
                        }
                    } else coroutineScope.launch {
                        snackbarHostState.showSnackbar("Bitte zuerst Produkte auswählen.")
                    }
                },
                enabled = ausgewaehlt.isNotEmpty() && selectedTag.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saveButton"),     // ← für AndroidTest,
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
    }

    selectedLebensmittel?.let { produkt ->
        AlertDialog(
            onDismissRequest = { selectedLebensmittel = null },
            confirmButton = {
                TextButton(onClick = {
                    val gramm = grammText.toFloatOrNull()
                    if (gramm != null && gramm > 0) {
                        ausgewaehlt.add(produkt to gramm)
                        selectedLebensmittel = null; grammText = ""; grammFehler = false
                    } else {
                        grammFehler = true
                    }
                }) { Text("Hinzufügen", color = Color(0xFF81C784)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedLebensmittel = null; grammText = ""
                }) { Text("Abbrechen", color = Color.Red) }
            },
            title = { Text(produkt.produkt) },
            text = {
                OutlinedTextField(
                    value = grammText,
                    onValueChange = {
                        grammText = it; grammFehler = false
                    },
                    label = { Text("Menge in Gramm") },
                    isError = grammFehler,
                    supportingText = if (grammFehler) {
                        { Text("Bitte nur Zahlen eingeben", color = Color.Red) }
                    } else null,
                    modifier = Modifier.focusRequester(grammFocusRequester),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = if (grammFehler) Color.Red else Color(0xFF81C784),
                        unfocusedBorderColor = if (grammFehler) Color.Red else Color(0xFF81C784)
                    )
                )
            }
        )

        LaunchedEffect(produkt) {
            grammFocusRequester.requestFocus()
        }
    }
}