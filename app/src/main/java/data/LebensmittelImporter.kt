package com.smartwellness.data

import android.content.Context
import android.util.Log
import com.opencsv.CSVReaderBuilder
import com.smartwellness.data.entities.Lebensmittel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.math.RoundingMode


object LebensmittelImporter {
    suspend fun importCSVToDatabase(context: Context, db: AppDatabase) {
        withContext(Dispatchers.IO) {
            val dao = db.lebensmittelDao()
            dao.deleteAll()

            val lebensmittelListe = mutableListOf<Lebensmittel>()

            val inputStream = context.assets.open("Lebensmittelliste.csv")
            Log.d("CSV_IMPORT", "CSV-Datei erfolgreich geöffnet!")

            val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                .withSkipLines(1) // Kopfzeile überspringen
                .withCSVParser(
                    com.opencsv.CSVParserBuilder()
                        .withSeparator(',')
                        .withQuoteChar('"')
                        .build()
                )
                .build()

            var zeile = reader.readNext()
            while (zeile != null) {
                Log.d("CSV_IMPORT", "Zeile: ${zeile.joinToString()}")

                if (zeile.size >= 8) {
                    val lebensmittel = Lebensmittel(
                        gruppe = zeile[1].trim(),
                        produkt = zeile[2].trim(),
                        kalorien = zeile[3].replace(",", ".").toBigDecimalOrNull()
                            ?.setScale(1, RoundingMode.HALF_UP)?.toPlainString() ?: "0.0",
                        fett = zeile[4].replace(",", ".").toBigDecimalOrNull()
                            ?.setScale(1, RoundingMode.HALF_UP)?.toPlainString() ?: "0.0",
                        eiweiss = zeile[5].replace(",", ".").toBigDecimalOrNull()
                            ?.setScale(1, RoundingMode.HALF_UP)?.toPlainString() ?: "0.0",
                        kohlenhydrate = zeile[6].replace(",", ".").toBigDecimalOrNull()
                            ?.setScale(1, RoundingMode.HALF_UP)?.toPlainString() ?: "0.0",
                        glyk_index = zeile[7].toIntOrNull() ?: 0
                    )
                    lebensmittelListe.add(lebensmittel)
                }

                zeile = reader.readNext()
            }

            dao.insertAll(lebensmittelListe)

            Log.d("CSV_IMPORT", "Es wurden ${lebensmittelListe.size} Lebensmittel importiert.")
        }
    }
}