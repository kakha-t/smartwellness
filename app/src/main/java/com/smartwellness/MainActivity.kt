package com.smartwellness

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.smartwellness.data.AppDatabase
import com.smartwellness.data.DatabaseHolder
import com.smartwellness.data.LebensmittelImporter
import com.smartwellness.data.entities.Lebensmittel
import com.smartwellness.ui.theme.SmartWellnessTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase initialisieren
        FirebaseApp.initializeApp(this)

        // Room-DB initialisieren (nur einmalig)
        DatabaseHolder.init(applicationContext)
        val db: AppDatabase = DatabaseHolder.db

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