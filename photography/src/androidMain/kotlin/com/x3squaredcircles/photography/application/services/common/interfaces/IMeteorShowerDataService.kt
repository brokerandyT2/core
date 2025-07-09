// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/interfaces/IMeteorShowerDataService.kt
package com.x3squaredcircles.photography.application.common.interfaces

import com.x3squaredcircles.photography.domain.entities.MeteorShower
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.Job

/**
 * Service interface for accessing meteor shower data
 */
interface IMeteorShowerDataService {
    /**
     * Gets all meteor showers active on the specified date
     */
    suspend fun getActiveShowersAsync(
        date: LocalDate,
        cancellationToken: Job = Job()
    ): List<MeteorShower>

    /**
     * Gets meteor showers active on the specified date with minimum ZHR threshold
     */
    suspend fun getActiveShowersAsync(
        date: LocalDate,
        minZHR: Int,
        cancellationToken: Job = Job()
    ): List<MeteorShower>

    /**
     * Gets a specific meteor shower by its code identifier
     */
    suspend fun getShowerByCodeAsync(
        code: String,
        cancellationToken: Job = Job()
    ): MeteorShower?

    /**
     * Gets all available meteor showers in the database
     */
    suspend fun getAllShowersAsync(
        cancellationToken: Job = Job()
    ): List<MeteorShower>

    /**
     * Gets meteor showers that will be active within the specified date range
     */
    suspend fun getShowersInDateRangeAsync(
        startDate: LocalDate,
        endDate: LocalDate,
        cancellationToken: Job = Job()
    ): List<MeteorShower>

    /**
     * Checks if a specific meteor shower is active on the given date
     */
    suspend fun isShowerActiveAsync(
        showerCode: String,
        date: LocalDate,
        cancellationToken: Job = Job()
    ): Boolean

    /**
     * Gets the expected Zenith Hourly Rate (ZHR) for a shower on a specific date
     */
    suspend fun getExpectedZHRAsync(
        showerCode: String,
        date: LocalDate,
        cancellationToken: Job = Job()
    ): Double
}