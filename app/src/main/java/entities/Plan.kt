package com.smartwellness.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plan")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tag: String,
    val erstelltAm: String,
    val aktualisiertAm: String? = null,
    val datenJson: String,
    val userId: Int
)