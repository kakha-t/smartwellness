package com.smartwellness.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseHolder {

    lateinit var db: AppDatabase

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS plan (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    tag TEXT NOT NULL,
                    erstelltAm TEXT NOT NULL,
                    aktualisiertAm TEXT,
                    datenJson TEXT NOT NULL,
                    userId INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    fun init(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "smartwellness.db"
        )
            .fallbackToDestructiveMigration() // << HIER hinzugefügt
            .build()
    }
}