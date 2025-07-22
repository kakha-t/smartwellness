package com.smartwellness.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartwellness.data.dao.UserDao
import com.smartwellness.data.entities.User

class UserRepository(private val userDao: UserDao) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(
        userId: Int,
        email: String,
        phone: String?,
        geburtstag: String?,
        password: String?
    ) {
        val existingUser = userDao.getUserById(userId)
        if (existingUser != null) {
            val updatedUser = existingUser.copy(
                email = email,
                phone = phone ?: "",
                geburtstag = geburtstag ?: "",
                password = password ?: ""
            )

            // 🔒 Firebase Auth aktualisieren (E-Mail & Passwort)
            auth.currentUser?.let { firebaseUser ->
                if (firebaseUser.email != email) {
                    firebaseUser.updateEmail(email).addOnFailureListener {
                        it.printStackTrace()
                    }
                }
                if (!password.isNullOrBlank()) {
                    firebaseUser.updatePassword(password).addOnFailureListener {
                        it.printStackTrace()
                    }
                }
            }

            // ☁️ Firestore-Daten als Map speichern – inklusive ID
            val userMap = mapOf(
                "id" to updatedUser.id,
                "vorname" to updatedUser.vorname,
                "nachname" to updatedUser.nachname,
                "email" to updatedUser.email,
                "phone" to updatedUser.phone,
                "geburtstag" to updatedUser.geburtstag,
                "password" to updatedUser.password
            )

            firestore.collection("users")
                .document(updatedUser.email)
                .set(userMap)
                .addOnFailureListener { it.printStackTrace() }

            // 🗂️ Room aktualisieren
            userDao.update(updatedUser)
        }
    }
}