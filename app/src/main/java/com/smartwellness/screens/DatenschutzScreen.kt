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
fun DatenschutzScreen(navController: NavController) {
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
                            text = "Datenschutzerklärung",
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
                title = "1. Allgemeines",
                text = "Diese Webseite dient ausschließlich der Präsentation eines studentischen Projekts ohne kommerzielle Zwecke."
            )
            TextSection(
                title = "2. Fiktive Inhalte und Testdaten",
                text = "Alle Inhalte und Daten sind rein fiktiv und dienen Demonstrationszwecken. Es besteht kein Bezug zu realen Personen."
            )
            TextSection(
                title = "3. Keine Datenerhebung",
                text = "Es werden keine echten personenbezogenen Daten gespeichert oder verarbeitet. Keine Cookies, IP-Protokollierung oder Tracking-Tools werden genutzt."
            )
            TextSection(
                title = "4. Benutzerkonten und Formulare (Demo)",
                text = "Eingaben auf dieser Seite dienen nur zur Demonstration. Es findet keine echte Verarbeitung oder Speicherung statt."
            )
            TextSection(
                title = "5. Externe Links",
                text = "Für externe Links sind die jeweiligen Anbieter verantwortlich. Beim Verlassen der Website gilt deren Datenschutzerklärung."
            )
            TextSection(
                title = "6. Verantwortliche Stelle",
                text = "Es handelt sich um ein akademisches Projekt ohne reale Datenverarbeitung."
            )
            TextSection(
                title = "7. Gültigkeit",
                text = "Diese Erklärung gilt bis Juni 2025 für das Hochschulprojekt und nicht für produktive Anwendungen."
            )
        }
    }
}

@Composable
fun TextSection(title: String, text: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF2E7D32),
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}