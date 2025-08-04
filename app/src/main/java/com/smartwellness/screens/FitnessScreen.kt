package com.smartwellness.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartwellness.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current

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
                            text = "Fitness & Bewegung",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                CategorySection(
                    title = "1. Fitness",
                    providers = listOf(
                        FitnessProvider("Fitness First", R.drawable.fitnessfirst, "https://www.fitnessfirst.de/clubs"),
                        FitnessProvider("Clever Fit", R.drawable.clever_fit, "https://www.clever-fit.com/de/"),
                        FitnessProvider("FitX", R.drawable.fitx, "https://www.fitx.de/"),
                        FitnessProvider("Kieser Training", R.drawable.kieser, "https://www.kieser.de/")
                    ),
                    uriHandler = uriHandler
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color(0xFF2E7D32)
                )
            }
            item {
                CategorySection(
                    title = "2. Yoga, Pilates, Muskeltraining",
                    providers = listOf(
                        FitnessProvider("YogaEasy", R.drawable.yogaeasy, "https://www.yogaeasy.de/artikel/yoga-fuer-anfaenger"),
                        FitnessProvider("Pilates Berlin", R.drawable.pilatesberlin, "https://pilatesberlin.de/en/"),
                        FitnessProvider("Pilates Bee", R.drawable.pilatesbee, "https://pilatesbee.de/"),
                        FitnessProvider("Fitnessoase Querfurt", R.drawable.fitnessoase, "https://www.fitnessoase-querfurt.de/gezieltes-muskeltraining/")
                    ),
                    uriHandler = uriHandler
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color(0xFF2E7D32)
                )
            }
            item {
                CategorySection(
                    title = "3. Aquatraining",
                    providers = listOf(
                        FitnessProvider("Pfitzenmeier", R.drawable.pfitzenmeier, "https://www.pfitzenmeier.de/kurse/aqua-fitness/"),
                        FitnessProvider("Holmes Place", R.drawable.holmesplace, "https://www.holmesplace.de/gruppenkurse-c/aqua")
                    ),
                    uriHandler = uriHandler
                )
            }
        }
    }
}

data class FitnessProvider(
    val name: String,
    val imageRes: Int,
    val url: String
)

@Composable
fun CategorySection(
    title: String,
    providers: List<FitnessProvider>,
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color(0xFF2E7D32),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 1000.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(providers) { provider ->

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val backgroundColor = if (isPressed) Color(0xFFA3F18F) else Color.White

            Column(
                modifier = Modifier
                    .width(120.dp)
                    .wrapContentHeight()
                    .background(backgroundColor, shape = MaterialTheme.shapes.medium)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        uriHandler.openUri(provider.url)
                    }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = provider.imageRes),
                    contentDescription = provider.name,
                    modifier = Modifier
                        .width(80.dp)
                        .height(90.dp)
                )
                Text(
                    text = provider.name,
                    fontSize = 10.sp,
                    color = Color(0xFF06460D),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}