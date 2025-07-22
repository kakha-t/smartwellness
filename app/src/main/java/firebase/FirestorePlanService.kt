package com.smartwellness.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.smartwellness.entities.Plan
import kotlinx.coroutines.tasks.await

class FirestorePlanService {
    private val db = FirebaseFirestore.getInstance()

    fun savePlanToFirebase(userEmail: String, plan: Plan) {
        val planMap = mapOf(
            "tag" to plan.tag,
            "datenJson" to plan.datenJson,
            "erstelltAm" to plan.erstelltAm,
            "aktualisiertAm" to (plan.aktualisiertAm ?: ""),
            "userId" to plan.userId
        )

        db.collection("tagesplaene").document("${userEmail}_${plan.tag}")
            .set(planMap)
            .addOnSuccessListener {
                Log.d("FirestorePlanService", "✅ Plan erfolgreich gespeichert.")
            }
            .addOnFailureListener {
                Log.e("FirestorePlanService", "❌ Fehler beim Speichern", it)
            }
    }

    fun deletePlanFromFirebase(userEmail: String, tag: String) {
        val docId = "${userEmail}_$tag"
        db.collection("tagesplaene").document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d("FirestorePlanService", "✅ Plan erfolgreich gelöscht.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestorePlanService", "❌ Fehler beim Löschen", e)
            }
    }

    suspend fun loadPlansFromFirebase(userId: Int, userEmail: String): List<Plan> {
        val snapshot = db.collection("tagesplaene")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            try {
                Plan(
                    id = 0,
                    userId = userId,
                    tag = doc.getString("tag") ?: return@mapNotNull null,
                    datenJson = doc.getString("datenJson") ?: return@mapNotNull null,
                    erstelltAm = doc.getString("erstelltAm") ?: return@mapNotNull null,
                    aktualisiertAm = doc.getString("aktualisiertAm")
                )
            } catch (e: Exception) {
                Log.e("FirestorePlanService", "⚠️ Fehler beim Parsen des Plans: ${e.localizedMessage}")
                null
            }
        }
    }
}