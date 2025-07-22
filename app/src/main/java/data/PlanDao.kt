package com.smartwellness.data

import androidx.room.*
import com.smartwellness.entities.Plan

@Dao
interface PlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplacePlan(plan: Plan)

    @Query("SELECT * FROM `plan` WHERE userId = :userId")
    suspend fun getPlansByUser(userId: Int): List<Plan>

    @Query("SELECT * FROM `plan` WHERE userId = :userId AND tag = :tag LIMIT 1")
    suspend fun getPlanByUserAndTag(userId: Int, tag: String): Plan?

    @Delete
    suspend fun deletePlan(plan: Plan)
}
