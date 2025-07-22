package com.smartwellness.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lebensmittel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gruppe: String,
    val produkt: String,
    val kalorien: String,
    val fett: String,
    val eiweiss: String,
    val kohlenhydrate: String,
    val glyk_index: Int
)