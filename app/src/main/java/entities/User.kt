package com.smartwellness.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false) // keine automatische ID
    val id: Int,
    val email: String,
    val vorname: String,
    val nachname: String,
    val phone: String,
    val geburtstag: String,
    val password: String
)
