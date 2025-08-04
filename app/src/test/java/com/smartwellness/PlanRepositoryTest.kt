package com.smartwellness

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.smartwellness.data.PlanDao
import com.smartwellness.data.PlanRepository
import com.smartwellness.entities.Plan

class PlanRepositoryTest {

    // 1) Mock-DAO anlegen (alle Suspend-Funktionen laufen still)
    private val dao = mockk<PlanDao>(relaxed = true)

    // 2) Repository mit Mock erstellen
    private val repo = PlanRepository(dao)

    @Test
    fun `insertOrReplacePlan should call dao insertOrReplacePlan`() = runTest {
        // Arrange: Test-Objekt
        val samplePlan = Plan(
            id = 1,
            userId = 42,
            tag = "TestTag",
            datenJson = "[]",
            erstelltAm = "now",
            aktualisiertAm = null
        )

        // Act: aufrufen
        repo.insertOrReplacePlan(samplePlan)

        // Assert: dao.insertOrReplacePlan wurde genau mit samplePlan aufgerufen
        coVerify { dao.insertOrReplacePlan(samplePlan) }
    }

    @Test
    fun `getPlansByUser should call dao getPlansByUser`() = runTest {
        // Arrange: Stub, dass dao.getPlansByUser(any()) eine leere Liste liefert
        coEvery { dao.getPlansByUser(any()) } returns emptyList()

        // Act
        val result = repo.getPlansByUser(42)

        // Assert: dao.getPlansByUser wurde aufgerufen und wir bekommen das Ergebnis
        coVerify { dao.getPlansByUser(42) }
        assert(result.isEmpty())
    }
}