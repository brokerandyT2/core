// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/ISunService.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.models.SunPositionDto
import com.x3squaredcircles.photography.domain.models.SunTimesDto

import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface ISunService {
    /**
     * Calculates sun position (azimuth and elevation) for the specified coordinates and date/time
     */
    suspend fun getSunPositionAsync(
        latitude: Double,
        longitude: Double,
        dateTime: LocalDateTime,
        cancellationToken: Job = Job()
    ): Result<SunPositionDto>

    /**
     * Calculates sun times (sunrise, sunset, dawn, dusk, etc.) for the specified coordinates and date
     */
    suspend fun getSunTimesAsync(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        cancellationToken: Job = Job()
    ): Result<SunTimesDto>
}