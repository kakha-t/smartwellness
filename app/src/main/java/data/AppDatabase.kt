package com.smartwellness.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartwellness.data.dao.UserDao
import com.smartwellness.data.entities.User
import com.smartwellness.data.entities.Lebensmittel
import com.smartwellness.entities.Plan

@Database(
    entities = [User::class, Lebensmittel::class, Plan::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lebensmittelDao(): LebensmittelDao
    abstract fun planDao(): PlanDao
}
