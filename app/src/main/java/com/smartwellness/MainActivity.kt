package com.smartwellness

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartwellness.data.AppDatabase
import com.smartwellness.data.DatabaseHolder
import com.smartwellness.data.LebensmittelImporter
import com.smartwellness.data.entities.Lebensmittel
import com.smartwellness.ui.theme.SmartWellnessTheme
import com.google.firebase.FirebaseApp // 🔥 Wichtig für Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Firebase initialisieren
        FirebaseApp.initializeApp(this)

        // Migration vorbereiten
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Falls du keine Felder geändert hast → leer lassen oder Spalte ergänzen
            }
        }

        // Room-DB laden
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "smartwellness.db"
        )
            .addMigrations(MIGRATION_2_3)
            .build()

        DatabaseHolder.db = db

        val lebensmittelListe = mutableListOf<Lebensmittel>()

        lifecycleScope.launch(Dispatchers.IO) {
            // CSV importieren → in DB speichern
            LebensmittelImporter.importCSVToDatabase(applicationContext, db)

            // Alle Produkte laden
            val alleProdukte = db.lebensmittelDao().searchByProdukt("")

            lebensmittelListe.clear()
            lebensmittelListe.addAll(alleProdukte)

            alleProdukte.forEach {
                Log.d("LebensmittelCheck", it.toString())
            }

            // UI anzeigen
            withContext(Dispatchers.Main) {
                setContent {
                    SmartWellnessTheme {
                        AppNavigation(
                            db = db,
                            lebensmittelListe = lebensmittelListe
                        )
                    }
                }
            }
        }
    }
}