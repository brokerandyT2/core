// com/x3squaredcircles/photography/application/errors/ExposureErrors.kt
package com.x3squaredcircles.photography.application.errors

/**
 * Base class for exposure-related errors
 */
abstract class ExposureError(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Error thrown when the calculated exposure is too bright (overexposed)
 */
class OverexposedError(val stopsOverexposed: Double) : ExposureError(
    "Image will be overexposed by approximately ${String.format("%.1f", stopsOverexposed)} stops"
)

/**
 * Error thrown when the calculated exposure is too dark (underexposed)
 */
class UnderexposedError(val stopsUnderexposed: Double) : ExposureError(
    "Image will be underexposed by approximately ${String.format("%.1f", stopsUnderexposed)} stops"
)

/**
 * Error thrown when a parameter exceeds the physical limits of the camera
 */
class ExposureParameterLimitError(
    val parameterName: String,
    val requestedValue: String,
    val availableLimit: String
) : ExposureError(
    "The requested $parameterName ($requestedValue) exceeds available limits. Maximum available: $availableLimit"
)