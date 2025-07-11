// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/FOVCalculationService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.application.services.IFOVCalculationService
import com.x3squaredcircles.photography.application.services.SensorDimensions
import com.x3squaredcircles.photography.application.services.OverlayBox
import com.x3squaredcircles.photography.application.services.Size
import com.x3squaredcircles.photography.repositories.ICameraBodyRepository
import com.x3squaredcircles.photography.domain.entities.PhoneCameraProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.math.* 

class FOVCalculationService(
    private val logger: ILoggingService,
    private val cameraBodyRepository: ICameraBodyRepository
) : IFOVCalculationService {



    override fun calculateHorizontalFOV(focalLength: Double, sensorWidth: Double): Double {
        if (focalLength <= 0 || sensorWidth <= 0) {
            throw IllegalArgumentException("Focal length and sensor width must be positive values")
        }

        val fovRadians = 2 * atan(sensorWidth / (2 * focalLength))
        return fovRadians * (180.0 / PI)
    }

    override fun calculateVerticalFOV(focalLength: Double, sensorHeight: Double): Double {
        if (focalLength <= 0 || sensorHeight <= 0) {
            throw IllegalArgumentException("Focal length and sensor height must be positive values")
        }

        val fovRadians = 2 * atan(sensorHeight / (2 * focalLength))
        return fovRadians * (180.0 / PI)
    }

    override suspend fun estimateSensorDimensionsAsync(phoneModel: String, cancellationToken: Job): Result<SensorDimensions> {
        return try {
            if (cancellationToken.isCancelled) {
                throw IllegalStateException("Operation was cancelled")
            }

            if (phoneModel.isBlank()) {
                return Result.failure("Phone model cannot be null or empty")
            }

            val searchResult = cameraBodyRepository.searchByNameAsync(phoneModel, cancellationToken)
            if (!searchResult.isSuccess) {
                logger.logError("Error searching camera bodies by name: $phoneModel", null)
                return Result.failure("Failed to search camera database: ${searchResult.data}")
            }

            val cameras = searchResult.data ?: emptyList()
            if (cameras.isNotEmpty()) {
                val camera = cameras.first()
                val sensorDimensions = SensorDimensions(
                    width = camera.sensorWidth,
                    height = camera.sensorHeight,
                    sensorType = camera.sensorType
                )
                logger.logDebug("Found sensor dimensions for $phoneModel: ${sensorDimensions.sensorType}")
                return Result.success(sensorDimensions)
            }

            logger.logDebug("No sensor dimensions found for phone model: $phoneModel")
            Result.failure("No sensor dimensions found for phone model: $phoneModel")
        } catch (ex: Exception) {
            logger.logError("Error estimating sensor dimensions for $phoneModel", ex)
            Result.failure("Failed to get sensor dimensions: ${ex.message}")
        }
    }

    override suspend fun createPhoneCameraProfileAsync(
        phoneModel: String,
        focalLength: Double,
        cancellationToken: Job
    ): Result<PhoneCameraProfile> {
        return try {
            if (cancellationToken.isCancelled) {
                throw IllegalStateException("Operation was cancelled")
            }

            if (phoneModel.isBlank()) {
                return Result.failure("Phone model cannot be null or empty")
            }

            if (focalLength <= 0) {
                return Result.failure("Focal length must be positive")
            }

            val sensorResult = estimateSensorDimensionsAsync(phoneModel, cancellationToken)
            if (!sensorResult.isSuccess) {
                return Result.failure("Failed to get sensor dimensions: ${sensorResult.data}")
            }

            val sensor = sensorResult.data!!
            val horizontalFOV = calculateHorizontalFOV(focalLength, sensor.width)

            val profile = PhoneCameraProfile(
                phoneModel,
                focalLength,
                horizontalFOV
            )

            logger.logInfo("Created phone camera profile for '$phoneModel': ${focalLength}mm, ${horizontalFOV}° FOV")
            
            Result.success(profile)
        } catch (ex: Exception) {
            logger.logError("Error creating phone camera profile: $phoneModel, focal length: $focalLength", ex)
            Result.failure("Error creating phone camera profile: ${ex.message}")
        }
    }

    override fun calculateOverlayBox(phoneFOV: Double, cameraFOV: Double, screenSize: Size): OverlayBox {
        if (phoneFOV <= 0 || cameraFOV <= 0) {
            throw IllegalArgumentException("FOV values must be positive")
        }

        if (screenSize.width <= 0 || screenSize.height <= 0) {
            throw IllegalArgumentException("Screen size must be positive")
        }

        val fovRatio = cameraFOV / phoneFOV
        
        val overlayWidth = (screenSize.width * fovRatio).toInt()
        val overlayHeight = (screenSize.height * fovRatio).toInt()
        
        val x = (screenSize.width - overlayWidth) / 2
        val y = (screenSize.height - overlayHeight) / 2

        return OverlayBox(
            x = maxOf(0, x),
            y = maxOf(0, y),
            width = minOf(overlayWidth, screenSize.width),
            height = minOf(overlayHeight, screenSize.height)
        )
    }
}