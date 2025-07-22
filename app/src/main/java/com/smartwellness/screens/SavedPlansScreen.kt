package com.smartwellness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartwellness.data.PlanRepository
import com.smartwellness.entities.Plan
import kotlinx.coroutines.launch
import org.json.JSONArray
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.smartwellness.firebase.FirestorePlanService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlansScreen(
    navController: NavController,
    userId: Int,
    userEmail: String,
    planRepository: PlanRepository
) {
    val plans = remember { mutableStateOf<List<Plan>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val firestorePlanService = remember { FirestorePlanService() }

    LaunchedEffect(userId) {
        plans.value = planRepository.getPlansByUser(userId)
    }

    val sortedPlans = plans.value.sortedWith(compareBy { dayOfWeekOrder(it.tag) })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meine gespeicherten Tagespläne",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("saved_plans") { inclusive = true }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(sortedPlans) { plan ->
                PlanCard(
                    plan = plan,
                    onDelete = {
                        coroutineScope.launch {
                            planRepository.deletePlan(plan)
                            FirestorePlanService().deletePlanFromFirebase(userEmail, plan.tag)
                            plans.value = planRepository.getPlansByUser(userId)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun dayOfWeekOrder(day: String): Int {
    return when (day.lowercase()) {
        "montag" -> 1
        "dienstag" -> 2
        "mittwoch" -> 3
        "donnerstag" -> 4
        "freitag" -> 5
        "samstag" -> 6
        "sonntag" -> 7
        else -> 99
    }
}

@Composable
fun PlanCard(
    plan: Plan,
    onDelete: () -> Unit
) {
    val lebensmittelListe = remember(plan) { parseJson(plan.datenJson) }
    val sum = calculateSum(lebensmittelListe)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.tag,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Löschen",
                        tint = Color.Red
                    )
                }
            }

            Text(
                text = "Geändert am: ${plan.aktualisiertAm ?: plan.erstelltAm}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF81C784))
                    .padding(vertical = 4.dp)
            ) {
                TableCell("Produkt", modifier = Modifier.weight(2f), color = Color.White, bold = true)
                TableCell("Gramm", modifier = Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("kcal", modifier = Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("Fett", modifier = Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("Eiweiß", modifier = Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("KH", modifier = Modifier.weight(1f), color = Color.White, bold = true)
                TableCell("GI", modifier = Modifier.weight(1f), color = Color.White, bold = true)
            }

            lebensmittelListe.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    TableCell(it.produkt, modifier = Modifier.weight(2f))
                    TableCell("%.1f".format(it.gramm), modifier = Modifier.weight(1f))
                    TableCell(it.kcal, modifier = Modifier.weight(1f))
                    TableCell(it.fett, modifier = Modifier.weight(1f))
                    TableCell(it.eiweiss, modifier = Modifier.weight(1f))
                    TableCell(it.kh, modifier = Modifier.weight(1f))
                    TableCell(it.gi.toString(), modifier = Modifier.weight(1f))
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
                TableCell("Summierte Werte", modifier = Modifier.weight(2f), color = Color(0xFF2E7D32), bold = true)
                TableCell("%.1f".format(sum.gramm), modifier = Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.kcal, modifier = Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.fett, modifier = Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.eiweiss, modifier = Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell(sum.kh, modifier = Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
                TableCell("-", modifier = Modifier.weight(1f), color = Color(0xFF2E7D32), bold = true)
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
        modifier = modifier.padding(horizontal = 4.dp),
        color = color,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = 12.sp
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