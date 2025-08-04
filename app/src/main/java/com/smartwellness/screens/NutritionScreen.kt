package com.smartwellness.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartwellness.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(navController: NavController) {
    val context = LocalContext.current

    val anbieterList = listOf(
        Anbieter(
            name = "BMEL",
            imageRes = R.drawable.bmel,
            kurztext = "Infos vom Bundesministerium für Ernährung und Landwirtschaft.",
            langtext = "Verbraucherinnen und Verbraucher bekommen in Deutschland ein breites Angebot an qualitativ hochwertigen Lebensmitteln. Gesundes Essen ist Genuss – und eine ausgewogene Ernährung ist das beste Rezept für eine gute Gesundheit. Das Bundesministerium für Ernährung und Landwirtschaft (BMEL) verfolgt eine ganzheitlich ausgerichtete Politik für eine gesunde Ernährung.",
            url = "https://www.bmel.de/DE/themen/ernaehrung/gesunde-ernaehrung/gesunde-ernaehrung_node.html"
        ),
        Anbieter(
            name = "Gutekueche.de",
            imageRes = R.drawable.gutekueche,
            kurztext = "Gesunde & Bio-Rezepte zum Nachkochen.",
            langtext = "Die Website von GuteKueche.de ist ein Rezept-Portal und bietet kostenlose Koch- und Backrezepte, Rezepte für Cocktails und listet Restaurants und Gastronomiebetriebe sowie Infos zu Wein und Winzer aus ganz Deutschland.",
            url = "https://www.gutekueche.de/gesunde-bio-rezepte"
        ),
        Anbieter(
            name = "EatBetter",
            imageRes = R.drawable.eatbetter,
            kurztext = "Nachhaltige Ernährung leicht gemacht.",
            langtext = "Einfach, genussvoll und dabei gesund und nachhaltig: Das ist die Idealvorstellung in Sachen Ernährung. Doch trotz wachsender Trends ist es nicht leicht, aufgrund der Masse an Informationen den Überblick zu behalten. eatbetter schafft Klarheit mit verlässlicher Qualität und hohem Nutzwert.",
            url = "https://www.eatbetter.de/nachhaltige-ernaehrung-tipps-tricks-fuer-den-alltag"
        ),
        Anbieter(
            name = "Ernährungs-Docs (NDR)",
            imageRes = R.drawable.ndr,
            kurztext = "Medizinisch geprüfte Rezepte & Tipps.",
            langtext = "Die Ernährungs-Docs, erfahrene Mediziner, wollen mit gezielten Ernährungsstrategien Symptome deutlich verbessern und Krankheiten sogar heilen.",
            url = "https://www.ndr.de/fernsehen/sendungen/die-ernaehrungsdocs/rezepte/index.html"
        ),
        Anbieter(
            name = "BZfE",
            imageRes = R.drawable.bzfe,
            kurztext = "Wissenschaftlich fundierte Ernährungshinweise.",
            langtext = "Genuss und Vielfalt mit pflanzlichen Lebensmitteln: Pflanzliche Lebensmittel spielen die Hauptrolle bei einer pflanzenbetonten Ernährung. Tierische Lebensmittel werden ergänzt. Das schützt die Gesundheit, schont die Erde und bietet Vielfalt.",
            url = "https://www.bzfe.de/"
        ),
        Anbieter(
            name = "EatSmarter",
            imageRes = R.drawable.eatsmarter,
            kurztext = "Gesunde, einfache Rezepte für jeden Tag.",
            langtext = "Finde über 100.000 gesunde Rezepte auf EAT SMARTER. Viele dieser Rezepte wurden von Profiköchen entwickelt und geprüft. So wird gesunde Ernährung ganz einfach.",
            url = "https://eatsmarter.de/rezepte"
        ),
        Anbieter(
            name = "DGE Empfehlungen",
            imageRes = R.drawable.dge,
            kurztext = "Deutsche Gesellschaft für Ernährung – wissenschaftlich geprüft.",
            langtext = "Bunt und gesund essen und dabei die Umwelt schonen – das sind die Empfehlungen der DGE. Eine Ernährung mit viel Obst, Gemüse, Vollkorngetreide, Hülsenfrüchten und pflanzlichen Ölen schützt die Gesundheit und die Ressourcen der Erde.",
            url = "https://www.dge.de/gesunde-ernaehrung/gut-essen-und-trinken/dge-empfehlungen/"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ernährung & Rezepte",
                            color = Color(0xFF06460D),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            anbieterList.forEach { item ->
                AnbieterCard(item) { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AnbieterCard(anbieter: Anbieter, onClickLink: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickLink(anbieter.url) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = anbieter.imageRes),
                contentDescription = "${anbieter.name} Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = anbieter.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = anbieter.kurztext,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = anbieter.langtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        }
    }
}

data class Anbieter(
    val name: String,
    val imageRes: Int,
    val kurztext: String,
    val langtext: String,
    val url: String
)