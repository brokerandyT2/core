// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/services/ITimezoneServiceExtension.kt
package com.x3squaredcircles.photography.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ITimezoneService
import kotlinx.coroutines.Job

suspend fun ITimezoneService.getTimezoneFromCoordinatesAsync(
    latitude: Double,
    longitude: Double,
    cancellationToken: Job = Job()
): Result<String> {
    return try {
        // Simple timezone calculation based on longitude
        val utcOffset = (longitude / 15.0).toInt()
        val timezoneId = when {
            utcOffset <= -10 -> "Pacific/Honolulu"
            utcOffset <= -8 -> "America/Los_Angeles"
            utcOffset <= -7 -> "America/Denver"
            utcOffset <= -6 -> "America/Chicago"
            utcOffset <= -5 -> "America/New_York"
            utcOffset <= 0 -> "Europe/London"
            utcOffset <= 1 -> "Europe/Paris"
            utcOffset <= 2 -> "Europe/Berlin"
            utcOffset <= 9 -> "Asia/Tokyo"
            utcOffset <= 10 -> "Australia/Sydney"
            else -> "UTC"
        }
        Result.Success(timezoneId)
    } catch (e: Exception) {
        Result.Failure("Unable to determine timezone: ${e.message}")
    }
}