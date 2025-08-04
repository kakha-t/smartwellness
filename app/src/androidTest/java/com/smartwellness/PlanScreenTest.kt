package com.smartwellness

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.navigation.compose.rememberNavController
import com.smartwellness.data.PlanDao
import com.smartwellness.data.PlanRepository
import com.smartwellness.entities.Plan
import com.smartwellness.screens.PlanScreen
import org.junit.Rule
import org.junit.Test


class PlanScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsMessage_andSaveButtonDisabled() {
        // Dummy-DAO, weil wir hier nur das UI testen
        val dummyDao = object : PlanDao {
            override suspend fun insertOrReplacePlan(plan: Plan) = Unit
            override suspend fun getPlansByUser(userId: Int) = emptyList<Plan>()
            override suspend fun getPlanByUserAndTag(userId: Int, tag: String) = null
            override suspend fun deletePlan(plan: Plan) = Unit
        }
        val repo = PlanRepository(dummyDao)

        // 1) Composable mit leerer Produktauswahl rendern
        composeTestRule.setContent {
            PlanScreen(
                navController     = rememberNavController(),
                lebensmittelListe = emptyList(),
                userEmail         = "",
                userId            = 0,
                planRepository    = repo,
                onSavePlan        = { /* no-op */ }
            )
        }

        // 2) Prüfen: Text für leere Auswahl ist sichtbar
        composeTestRule
            .onNodeWithText("Noch keine Produkte ausgewählt.")
            .assertIsDisplayed()

        // 3) Prüfen: Save-Button ist deaktiviert
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}