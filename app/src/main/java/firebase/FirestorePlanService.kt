package com.smartwellness.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.smartwellness.entities.Plan
import kotlinx.coroutines.tasks.await

class FirestorePlanService {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestorePlanService"

    /**
     * Speichert einen Plan in Firestore.
     * Dokument-ID wird aus E-Mail + Tag generiert, um Eindeutigkeit zu garantieren.
     */
    fun savePlanToFirebase(userEmail: String, plan: Plan) {
        val planMap = mapOf(
            "tag" to plan.tag,
            "datenJson" to plan.datenJson,
            "erstelltAm" to plan.erstelltAm,
            "aktualisiertAm" to (plan.aktualisiertAm ?: ""),
            "userId" to plan.userId
        )

        val docId = "${userEmail}_${plan.tag}"
        db.collection("tagesplaene").document(docId)
            .set(planMap)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Plan erfolgreich gespeichert.")
            }
            .addOnFailureListener {
                Log.e(TAG, "❌ Fehler beim Speichern", it)
            }
    }

    /**
     * Löscht einen Plan aus Firestore anhand von E-Mail und Tag.
     */
    fun deletePlanFromFirebase(userEmail: String, tag: String) {
        val docId = "${userEmail}_$tag"
        db.collection("tagesplaene").document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "✅ Plan erfolgreich gelöscht.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Fehler beim Löschen", e)
            }
    }

    /**
     * Lädt alle Pläne eines Benutzers aus Firestore anhand der userId.
     * Gibt eine Liste von [Plan]-Objekten zurück.
     */
    suspend fun loadPlansFromFirebase(userId: Int, userEmail: String): List<Plan> {
        return try {
            val snapshot = db.collection("tagesplaene")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    Plan(
                        id = 0, // Wird in Room gesetzt
                        userId = userId,
                        tag = doc.getString("tag") ?: return@mapNotNull null,
                        datenJson = doc.getString("datenJson") ?: return@mapNotNull null,
                        erstelltAm = doc.getString("erstelltAm") ?: return@mapNotNull null,
                        aktualisiertAm = doc.getString("aktualisiertAm")
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "⚠️ Fehler beim Parsen des Plans: ${e.localizedMessage}")
                    null
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Fehler beim Laden der Pläne: ${e.localizedMessage}")
            emptyList()
        }
    }
}