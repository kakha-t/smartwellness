package com.smartwellness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.clickable


@Composable
fun ZugangScreen(navController: NavController) {
    var datenschutzAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Individueller Plan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registrieren Sie sich oder loggen Sie sich ein!",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Box grün hinterlegt
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFDFFFD9), shape = RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Neu auf SmartWellness?",
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("register") },
                    enabled = datenschutzAccepted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA3F18F),
                        disabledContainerColor = Color(0xFFA3F18F).copy(alpha = 0.5f)
                    )
                ) {
                    Text(text = "Registrieren", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ich habe bereits ein Smartwellness-Konto",
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    enabled = datenschutzAccepted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                ) {
                    Text(text = "Einloggen", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = datenschutzAccepted,
                        onCheckedChange = { datenschutzAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFA3F18F)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Ich akzeptiere die",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Datenschutzbestimmungen",
                        color = Color.Blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.clickable {
                            navController.navigate("datenschutz")
                        }
                    )
                }
            }
        }
    }
}
