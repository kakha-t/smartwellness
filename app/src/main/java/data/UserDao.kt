package com.smartwellness.data.dao

import androidx.room.*
import com.smartwellness.data.entities.User

@Dao
interface UserDao {

    // Vorhandene Funktion beibehalten
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    // Vorhandene Funktion beibehalten
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Vorhandene Funktion beibehalten
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    // NEU → für MeinKontoScreen
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    // NEU → für MeinKontoScreen
    @Update
    suspend fun update(user: User)

    @Query("SELECT MAX(id) FROM users")
    suspend fun getMaxUserId(): Int?
}
