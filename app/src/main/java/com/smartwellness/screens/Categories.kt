package com.smartwellness.screens

import com.smartwellness.R

data class CategoryItem(
    val imageRes: Int,
    val title: String,
    val description: String,
    val route: String
)

val categoryItems = listOf(
    CategoryItem(
        imageRes = R.drawable.gesunde_ernaehrung,
        title = "Gesunde Ernährung",
        description = "Finde köstliche, ausgewogene Rezepte.",
        route = "nutrition"
    ),
    CategoryItem(
        imageRes = R.drawable.fitness_bewegung,
        title = "Fitness & Bewegung",
        description = "Trainings-Programme entdecken.",
        route = "fitness"
    ),
    CategoryItem(
        imageRes = R.drawable.individuelle_plane,
        title = "Individueller Plan",
        description = "Eigene Pläne erstellen.",
        route = "zugang"
    )
)
