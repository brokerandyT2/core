// com/x3squaredcircles/photography/infrastructure/services/ExposureCalculatorService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.application.errors.ExposureError
import com.x3squaredcircles.photography.application.services.IExposureCalculatorService
import com.x3squaredcircles.photography.application.services.ShutterSpeeds
import com.x3squaredcircles.photography.application.services.Apertures
import com.x3squaredcircles.photography.application.services.ISOs
import com.x3squaredcircles.photography.application.services.models.ExposureTriangleDto
import com.x3squaredcircles.photography.application.services.models.ExposureSettingsDto
import com.x3squaredcircles.photography.application.services.models.ExposureIncrements
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ExposureCalculatorService(
    private val exposureTriangleService: IExposureTriangleService
) : IExposureCalculatorService {

    override suspend fun calculateShutterSpeedAsync(
        baseExposure: ExposureTriangleDto,
        targetAperture: String,
        targetIso: String,
        increments: ExposureIncrements,
        cancellationToken: Job,
        evCompensation: Double
    ): Result<ExposureSettingsDto> {
        return try {
            // Move intensive calculations to background thread to prevent UI blocking
            val result = withContext(Dispatchers.Default) {
                val scale = getIncrementScale(increments)
                val shutterSpeed = exposureTriangleService.calculateShutterSpeed(
                    baseExposure.shutterSpeed,
                    baseExposure.aperture,
                    baseExposure.iso,
                    targetAperture,
                    targetIso,
                    scale,
                    evCompensation
                )
                
                ExposureSettingsDto(
                    shutterSpeed = shutterSpeed,
                    aperture = targetAperture,
                    iso = targetIso
                )
            }
            
            Result.success(result)
        } catch (ex: ExposureError) {
            Result.failure(ex.message ?: "Exposure error calculating shutter speed")
        } catch (ex: Exception) {
            Result.failure("Error calculating shutter speed: ${ex.message}")
        }
    }

    override suspend fun calculateApertureAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetIso: String,
        increments: ExposureIncrements,
        cancellationToken: Job,
        evCompensation: Double
    ): Result<ExposureSettingsDto> {
        return try {
            // Move intensive calculations to background thread to prevent UI blocking
            val result = withContext(Dispatchers.Default) {
                val scale = getIncrementScale(increments)
                val aperture = exposureTriangleService.calculateAperture(
                    baseExposure.shutterSpeed,
                    baseExposure.aperture,
                    baseExposure.iso,
                    targetShutterSpeed,
                    targetIso,
                    scale,
                    evCompensation
                )
                
                ExposureSettingsDto(
                    shutterSpeed = targetShutterSpeed,
                    aperture = aperture,
                    iso = targetIso
                )
            }
            
            Result.success(result)
        } catch (ex: ExposureError) {
            Result.failure(ex.message ?: "Exposure error calculating aperture")
        } catch (ex: Exception) {
            Result.failure("Error calculating aperture: ${ex.message}")
        }
    }

    override suspend fun calculateIsoAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetAperture: String,
        increments: ExposureIncrements,
        cancellationToken: Job,
        evCompensation: Double
    ): Result<ExposureSettingsDto> {
        return try {
            // Move intensive calculations to background thread to prevent UI blocking
            val result = withContext(Dispatchers.Default) {
                val scale = getIncrementScale(increments)
                val iso = exposureTriangleService.calculateIso(
                    baseExposure.shutterSpeed,
                    baseExposure.aperture,
                    baseExposure.iso,
                    targetShutterSpeed,
                    targetAperture,
                    scale,
                    evCompensation
                )
                
                ExposureSettingsDto(
                    shutterSpeed = targetShutterSpeed,
                    aperture = targetAperture,
                    iso = iso
                )
            }
            
            Result.success(result)
        } catch (ex: ExposureError) {
            Result.failure(ex.message ?: "Exposure error calculating ISO")
        } catch (ex: Exception) {
            Result.failure("Error calculating ISO: ${ex.message}")
        }
    }

    override suspend fun getShutterSpeedsAsync(
        increments: ExposureIncrements,
        cancellationToken: Job
    ): Result<Array<String>> {
        return try {
            // Move array retrieval to background thread for consistency and prevent potential blocking
            val shutterSpeeds = withContext(Dispatchers.Default) {
                val step = increments.toString()
                ShutterSpeeds.getScale(step)
            }
            
            Result.success(shutterSpeeds)
        } catch (ex: Exception) {
            Result.failure("Error retrieving shutter speeds: ${ex.message}")
        }
    }

    override suspend fun getAperturesAsync(
        increments: ExposureIncrements,
        cancellationToken: Job
    ): Result<Array<String>> {
        return try {
            // Move array retrieval to background thread for consistency and prevent potential blocking
            val apertures = withContext(Dispatchers.Default) {
                val step = increments.toString()
                Apertures.getScale(step)
            }
            
            Result.success(apertures)
        } catch (ex: Exception) {
            Result.failure("Error retrieving apertures: ${ex.message}")
        }
    }

    override suspend fun getIsosAsync(
        increments: ExposureIncrements,
        cancellationToken: Job
    ): Result<Array<String>> {
        return try {
            // Move array retrieval to background thread for consistency and prevent potential blocking
            val isos = withContext(Dispatchers.Default) {
                val step = increments.toString()
                ISOs.getScale(step)
            }
            
            Result.success(isos)
        } catch (ex: Exception) {
            Result.failure("Error retrieving ISOs: ${ex.message}")
        }
    }

    private fun getIncrementScale(increments: ExposureIncrements): Int {
        return when (increments) {
            ExposureIncrements.Full -> 1
            ExposureIncrements.Half -> 2
            ExposureIncrements.Third -> 3
        }
    }
}