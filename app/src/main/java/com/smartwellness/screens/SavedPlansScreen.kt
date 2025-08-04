package com.smartwellness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartwellness.data.PlanRepository
import com.smartwellness.entities.Plan
import com.smartwellness.firebase.FirestorePlanService
import kotlinx.coroutines.launch
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlansScreen(
    navController: NavController,
    userId: Int,
    userEmail: String,
    planRepository: PlanRepository
) {
    val state = remember { mutableStateOf(UiState<List<Plan>>(emptyList())) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        state.value = state.value.copy(loading = true)
        try {
            val loadedPlans = planRepository.getPlansByUser(userId)
            state.value = UiState(data = loadedPlans.sortedBy { dayOfWeekOrder(it.tag) }, loading = false)
        } catch (e: Exception) {
            state.value = UiState(data = emptyList(), error = "Fehler beim Laden der Pläne", loading = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meine gespeicherten Tagespläne",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = Color(0xFF4CAF50))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("saved_plans") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFF2E7D32))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.value.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }
        } else if (state.value.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("❌ ${state.value.error}", color = Color.Red)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8F5E9))
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(state.value.data) { plan ->
                    PlanCard(
                        plan = plan,
                        onDelete = {
                            coroutineScope.launch {
                                planRepository.deletePlan(plan)
                                FirestorePlanService().deletePlanFromFirebase(userEmail, plan.tag)
                                val reloaded = planRepository.getPlansByUser(userId)
                                state.value = UiState(data = reloaded.sortedBy { dayOfWeekOrder(it.tag) })
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

data class UiState<T>(
    val data: T,
    val loading: Boolean = false,
    val error: String? = null
)

@Composable
fun PlanCard(
    plan: Plan,
    onDelete: () -> Unit
) {
    val lebensmittelListe = remember(plan) { parseJson(plan.datenJson) }
    val sum = calculateSum(lebensmittelListe)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    plan.tag,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    fontSize = 22.sp
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = Color.Red)
                }
            }

            Text(
                text = "Geändert am: ${plan.aktualisiertAm ?: plan.erstelltAm}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF81C784))
                    .padding(4.dp)
            ) {
                TableCell("Produkt", Modifier.weight(2f), color = Color.White, bold = true)
                TableCell("Gramm", Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("kcal", Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("Fett", Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("Eiweiß", Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("KH", Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("GI", Modifier.weight(1f), color = Color.White, bold = true)
            }

            lebensmittelListe.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    TableCell(it.produkt, Modifier.weight(2f))
                    TableCell("%.1f".format(it.gramm), Modifier.weight(1f))
                    TableCell(it.kcal, Modifier.weight(1f))
                    TableCell(it.fett, Modifier.weight(1f))
                    TableCell(it.eiweiss, Modifier.weight(1f))
                    TableCell(it.kh, Modifier.weight(1f))
                    TableCell(it.gi.toString(), Modifier.weight(1f))
                }
            }

            HorizontalDivider(
                color = Color(0xFF81C784),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(vertical = 4.dp)
            ) {
                TableCell("Summierte Werte", Modifier.weight(2f), color = Color(0xFF2E7D32), bold = true)
                TableCell("%.1f".format(sum.gramm), Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.kcal, Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.fett, Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.eiweiss, Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.kh, Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell("-", Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    bold: Boolean = false
) {
    Text(
        text = text,
        color = color,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = 12.sp,
        modifier = modifier.padding(4.dp)
    )
}

data class LebensmittelDisplayRow(
    val produkt: String,
    val gramm: Float,
    val kcal: String,
    val fett: String,
    val eiweiss: String,
    val kh: String,
    val gi: Int
)

fun parseJson(json: String): List<LebensmittelDisplayRow> {
    val list = mutableListOf<LebensmittelDisplayRow>()
    try {
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                LebensmittelDisplayRow(
                    produkt = obj.optString("produkt"),
                    gramm = obj.optDouble("menge", 0.0).toFloat(),
                    kcal = obj.optString("kcal"),
                    fett = obj.optString("fett"),
                    eiweiss = obj.optString("eiweiss"),
                    kh = obj.optString("kh"),
                    gi = obj.optInt("glyk_index")
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

data class SumRow(
    val gramm: Float,
    val kcal: String,
    val fett: String,
    val eiweiss: String,
    val kh: String
)

fun calculateSum(list: List<LebensmittelDisplayRow>): SumRow {
    val sumGramm = list.sumOf { it.gramm.toDouble() }.toFloat()
    val sumKcal = list.sumOf { it.kcal.toFloatOrNull()?.toDouble() ?: 0.0 }.toFloat()
    val sumFett = list.sumOf { it.fett.toFloatOrNull()?.toDouble() ?: 0.0 }.toFloat()
    val sumEw = list.sumOf { it.eiweiss.toFloatOrNull()?.toDouble() ?: 0.0 }.toFloat()
    val sumKh = list.sumOf { it.kh.toFloatOrNull()?.toDouble() ?: 0.0 }.toFloat()

    return SumRow(
        gramm = sumGramm,
        kcal = "%.1f".format(sumKcal),
        fett = "%.1f".format(sumFett),
        eiweiss = "%.1f".format(sumEw),
        kh = "%.1f".format(sumKh)
    )
}

fun dayOfWeekOrder(day: String): Int = when (day.lowercase()) {
    "montag" -> 1
    "dienstag" -> 2
    "mittwoch" -> 3
    "donnerstag" -> 4
    "freitag" -> 5
    "samstag" -> 6
    "sonntag" -> 7
    else -> 99
}