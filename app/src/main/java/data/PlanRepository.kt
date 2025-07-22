// PlanRepository.kt
package com.smartwellness.data

import com.smartwellness.entities.Plan

class PlanRepository(private val planDao: PlanDao) {

    suspend fun insertOrReplacePlan(plan: Plan) {
        planDao.insertOrReplacePlan(plan)
    }

    suspend fun insertOrReplacePlans(plans: List<Plan>) {
        plans.forEach { planDao.insertOrReplacePlan(it) }
    }

    suspend fun getPlansByUser(userId: Int): List<Plan> {
        return planDao.getPlansByUser(userId)
    }

    suspend fun getPlanByUserAndTag(userId: Int, tag: String): Plan? {
        return planDao.getPlanByUserAndTag(userId, tag)
    }

    suspend fun deletePlan(plan: Plan) {
        planDao.deletePlan(plan)
    }
}