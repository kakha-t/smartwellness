package com.smartwellness.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController,
    userId: Int?
) {
    var showLoginHint by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Mehr",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            MenuItem(
                title = "Mein Konto",
                onClick = {
                    if (userId != null) {
                        navController.navigate("mein_konto")
                    } else {
                        showLoginHint = true
                    }
                }
            )

            if (showLoginHint) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    buildAnnotatedString {
                        append("Sie sind nicht eingeloggt. ")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Hier anmelden")
                        }
                    },
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("login?returnTo=mein_konto")
                        }
                )
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFF81C784))

            MenuItem(
                title = "Datenschutz",
                onClick = { navController.navigate("datenschutz") }
            )
            HorizontalDivider(thickness = 1.dp, color = Color(0xFF81C784))

            MenuItem(
                title = "Impressum",
                onClick = { navController.navigate("impressum") }
            )
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.Black
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF2E7D32)
        )
    }
}