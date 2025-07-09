// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/IEquipmentRecommendationService.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.Result


import com.x3squaredcircles.photography.viewmodels.GenericEquipmentRecommendation
import com.x3squaredcircles.photography.viewmodels.UserEquipmentRecommendation
import com.x3squaredcircles.photography.viewmodels.HourlyEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.AstroTarget
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDateTime

interface IEquipmentRecommendationService {
    /**
     * Gets specific user equipment recommendations for astrophotography target
     */
    suspend fun getUserEquipmentRecommendationAsync(
        target: AstroTarget,
        cancellationToken: Job = Job()
    ): Result<UserEquipmentRecommendation>

    /**
     * Gets equipment recommendations for hourly predictions
     */
    suspend fun getHourlyEquipmentRecommendationsAsync(
        target: AstroTarget,
        predictionTimes: List<LocalDateTime>,
        cancellationToken: Job = Job()
    ): Result<List<HourlyEquipmentRecommendation>>

    /**
     * Gets generic equipment recommendations when no user equipment available
     */
    suspend fun getGenericRecommendationAsync(
        target: AstroTarget,
        cancellationToken: Job = Job()
    ): Result<GenericEquipmentRecommendation>
}