// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/IExposureCalculatorService.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.application.services.models.ExposureTriangleDto
import com.x3squaredcircles.photography.application.services.models.ExposureIncrements
import com.x3squaredcircles.photography.application.services.models.ExposureSettingsDto

import kotlinx.coroutines.Job

interface IExposureCalculatorService {
    /**
     * Calculates a new shutter speed based on the base exposure and desired aperture and ISO
     */
    suspend fun calculateShutterSpeedAsync(
        baseExposure: ExposureTriangleDto,
        targetAperture: String,
        targetIso: String,
        increments: ExposureIncrements,
        cancellationToken: Job = Job(),
        evCompensation: Double = 0.0
    ): Result<ExposureSettingsDto>

    /**
     * Calculates a new aperture based on the base exposure and desired shutter speed and ISO
     */
    suspend fun calculateApertureAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetIso: String,
        increments: ExposureIncrements,
        cancellationToken: Job = Job(),
        evCompensation: Double = 0.0
    ): Result<ExposureSettingsDto>

    /**
     * Calculates a new ISO based on the base exposure and desired shutter speed and aperture
     */
    suspend fun calculateIsoAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetAperture: String,
        increments: ExposureIncrements,
        cancellationToken: Job = Job(),
        evCompensation: Double = 0.0
    ): Result<ExposureSettingsDto>

    /**
     * Gets available shutter speed values for the specified increment
     */
    suspend fun getShutterSpeedsAsync(
        increments: ExposureIncrements,
        cancellationToken: Job = Job()
    ): Result<Array<String>>

    /**
     * Gets available aperture values for the specified increment
     */
    suspend fun getAperturesAsync(
        increments: ExposureIncrements,
        cancellationToken: Job = Job()
    ): Result<Array<String>>

    /**
     * Gets available ISO values for the specified increment
     */
    suspend fun getIsosAsync(
        increments: ExposureIncrements,
        cancellationToken: Job = Job()
    ): Result<Array<String>>
}