package com.smartwellness

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.smartwellness.data.AppDatabase
import com.smartwellness.data.PlanDao
import com.smartwellness.entities.Plan
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlanDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: PlanDao

    @Before
    fun setup() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.planDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndQueryByUserAndTag() = runBlocking {
        // Arrange
        val plan = Plan(
            id = 1,
            userId = 99,
            tag = "TestTag",
            datenJson = "[]",
            erstelltAm = "now",
            aktualisiertAm = null
        )

        // Act
        dao.insertOrReplacePlan(plan)
        val loaded = dao.getPlanByUserAndTag(99, "TestTag")

        // Assert
        Assert.assertNotNull("Plan sollte gefunden werden", loaded)
        Assert.assertEquals("TestTag", loaded!!.tag)
    }
}