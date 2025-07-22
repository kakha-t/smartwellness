package com.smartwellness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpressumScreen(navController: NavController) {
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
                            text = "Impressum",
                            color = Color(0xFF2E7D32),
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
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TextSection(
                title = "Angaben gemäß § 5 TMG",
                text = "Dieses Webprojekt wurde im Rahmen eines Hochschulstudiums erstellt und dient ausschließlich zu Demonstrations- und Präsentationszwecken. Es handelt sich nicht um ein kommerzielles Angebot."
            )
            TextSection(
                title = "Verantwortlich für den Inhalt nach § 55 Abs. 2 RStV",
                text = """
                    Kakha Tsimakurizde
                    SmartWellness – Studienprojekt
                    IU Internationale Hochschule
                    Echelon 26
                    44149 Dortmund
                    E-Mail: info@smartwellness-projekt.de (fiktiv)
                    Tel.: +49 123 4567890 (fiktiv)
                """.trimIndent()
            )
            TextSection(
                title = "Urheberrecht und verwendete Materialien",
                text = "Die im Projekt verwendeten Texte, Bilder und Gestaltungselemente stammen aus freien Quellen oder öffentlich zugänglichen Webseiten. Es wird keine kommerzielle Nutzung angestrebt. Inhalte dienen rein der Veranschaulichung. Bei irrtümlicher Verwendung bitten wir um Mitteilung – Inhalte werden entfernt."
            )
            TextSection(
                title = "Externe Links",
                text = "Für externe Links übernehmen wir keine Verantwortung. Für den Inhalt verlinkter Seiten sind ausschließlich deren Betreiber verantwortlich."
            )
            TextSection(
                title = "Hinweis",
                text = "Dieses Impressum gilt ausschließlich für das studentische Webprojekt „SmartWellness“ und besitzt keine rechtliche Verbindlichkeit im Sinne kommerzieller Telemedienangebote."
            )
        }
    }
}