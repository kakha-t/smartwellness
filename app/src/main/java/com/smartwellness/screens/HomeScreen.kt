package com.smartwellness.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartwellness.R

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            WaveBackground(color = Color(0xFFA3F18F))
        }

        item {
            Image(
                painter = painterResource(id = R.drawable.smartwellness_logo),
                contentDescription = "SmartWellness Logo",
                modifier = Modifier
                    .height(150.dp)
                    .padding(top = 0.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Inside
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Willkommen bei SmartWellness!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SmartWellness ist deine zentrale Anlaufstelle für alles rund um gesunde Ernährung, effektives Training und mentale Balance. Unsere Plattform bietet dir Zugang zu:",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BulletPoint("Ernährungspläne und Rezepten: Individuell auf deine Bedürfnisse und Vorlieben abgestimmt.")
                    BulletPoint("Vielseitige Trainingsmöglichkeiten: Yoga, Aqua-Training, Pilates, Kraft- und Ausdauertraining.")
                    BulletPoint("Individuelle Planerstellung: Stelle dir deinen eigenen Ernährungs- und Trainingsplan zusammen – täglich oder wöchentlich.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Starte noch heute und entdecke dein volles Potenzial!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Text(
                text = "Beliebte Kategorien",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categoryItems) { category ->
                    CategoryCard(
                        imageId = category.imageRes,
                        title = category.title,
                        description = category.description,
                        onClick = { navController.navigate(category.route) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
        Text("\u2022", fontSize = 18.sp, color = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
fun CategoryCard(
    imageId: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(180.dp)
            .height(220.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFA3F18F)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height * 0.6f)
            cubicTo(
                width * 0.1f, height * 0.01f,
                width * 0.7f, height * 0.9f,
                width, height * 0.5f
            )
            lineTo(width, 0f)
            lineTo(0f, 0f)
            close()
        }

        drawPath(
            path = path,
            color = color,
            style = Fill
        )
    }
}
