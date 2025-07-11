// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/application/services/SunService.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.models.SunPositionDto
import com.x3squaredcircles.photography.domain.models.SunTimesDto
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

class SunService(
    private val sunCalculatorService: ISunCalculatorService
) : ISunService {

    // Cache for sun calculations to improve performance and reduce redundant calculations
    private val sunDataCache = ConcurrentHashMap<String, Pair<Any, Instant>>()
    private val cacheTimeout = 15.minutes // Sun data changes relatively slowly

    override suspend fun getSunPositionAsync(
        latitude: Double,
        longitude: Double,
        dateTime: Long,
        cancellationToken: Job
    ): Result<SunPositionDto> {
        return try {
            // Create cache key for sun position
            val dateTimeInstant = Instant.fromEpochMilliseconds(dateTime)
            val cacheKey = "position_${dateTime}_${latitude.toString().take(8)}_${longitude.toString().take(8)}"

            // Check cache first to avoid redundant calculations
            val cached = sunDataCache[cacheKey]
            if (cached != null && Clock.System.now() < cached.second) {
                return Result.Success(cached.first as SunPositionDto)
            }

            // Move sun calculations to background thread to prevent UI blocking
            val result = withContext(Dispatchers.Default) {
                val localDateTime = dateTimeInstant.toLocalDateTime(TimeZone.currentSystemDefault())
                val timezone = TimeZone.currentSystemDefault().id

                val azimuth = sunCalculatorService.getSolarAzimuth(dateTimeInstant.toEpochMilliseconds(), latitude, longitude, timezone)
                val elevation = sunCalculatorService.getSolarElevation(dateTimeInstant.toEpochMilliseconds(), latitude, longitude, timezone)

                SunPositionDto(
                    azimuth = azimuth,
                    elevation = elevation,
                    dateTime = dateTimeInstant.toEpochMilliseconds(),
                    latitude = latitude,
                    longitude = longitude,
                    distance = 1.0 // Default distance in AU
                )
            }

            // Cache the result for future requests
            sunDataCache[cacheKey] = result to Clock.System.now().plus(cacheTimeout)

            Result.Success(result)
        } catch (ex: Exception) {
            Result.Failure("Error calculating sun position: ${ex.message}")
        }
    }

    override suspend fun getSunTimesAsync(
        latitude: Double,
        longitude: Double,
        date: Long,
        cancellationToken: Job
    ): Result<SunTimesDto> {
        return try {
            // Create cache key for sun times (daily data)
            val dateInstant = Instant.fromEpochMilliseconds(date)
            val localDate = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val cacheKey = "times_${localDate}_${latitude.toString().take(8)}_${longitude.toString().take(8)}"

            // Check cache first to avoid redundant calculations
            val cached = sunDataCache[cacheKey]
            if (cached != null && Clock.System.now() < cached.second) {
                return Result.Success(cached.first as SunTimesDto)
            }

            // Move sun calculations to background thread to prevent UI blocking
            val result = withContext(Dispatchers.Default) {
                val timezone = TimeZone.currentSystemDefault().id

                SunTimesDto(
                    date = localDate,
                    latitude = latitude,
                    longitude = longitude,
                    sunrise = longToLocalDateTime(sunCalculatorService.getSunrise(date, latitude, longitude, timezone)),
                    sunset = longToLocalDateTime(sunCalculatorService.getSunset(date, latitude, longitude, timezone)),
                    solarNoon = longToLocalDateTime(sunCalculatorService.getSolarNoon(date, latitude, longitude, timezone)),
                    astronomicalDawn = longToLocalDateTime(sunCalculatorService.getAstronomicalDawn(date, latitude, longitude, timezone)),
                    astronomicalDusk = longToLocalDateTime(sunCalculatorService.getAstronomicalDusk(date, latitude, longitude, timezone)),
                    nauticalDawn = longToLocalDateTime(sunCalculatorService.getNauticalDawn(date, latitude, longitude, timezone)),
                    nauticalDusk = longToLocalDateTime(sunCalculatorService.getNauticalDusk(date, latitude, longitude, timezone)),
                    civilDawn = longToLocalDateTime(sunCalculatorService.getCivilDawn(date, latitude, longitude, timezone)),
                    civilDusk = longToLocalDateTime(sunCalculatorService.getCivilDusk(date, latitude, longitude, timezone)),
                    goldenHourMorningStart = longToLocalDateTime(sunCalculatorService.getGoldenHourStart(date, latitude, longitude, timezone)),
                    goldenHourMorningEnd = longToLocalDateTime(sunCalculatorService.getGoldenHourEnd(date, latitude, longitude, timezone)),
                    goldenHourEveningStart = longToLocalDateTime(sunCalculatorService.getGoldenHourStart(date, latitude, longitude, timezone)), // Using same method for evening
                    goldenHourEveningEnd = longToLocalDateTime(sunCalculatorService.getGoldenHourEnd(date, latitude, longitude, timezone)) // Using same method for evening
                )
            }

            // Cache the result for future requests
            sunDataCache[cacheKey] = result to Clock.System.now().plus(cacheTimeout)

            Result.Success(result)
        } catch (ex: Exception) {
            Result.Failure("Error calculating sun times: ${ex.message}")
        }
    }
fun longToLocalDateTime(epochMillis: Long): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}
    /**
     * Clean up expired cache entries to prevent memory leaks
     */
    fun cleanupExpiredCache() {
        val now = Clock.System.now()
        val expiredKeys = sunDataCache.entries.filter { (_, cached: Pair<Any, Instant>) -> 
            now >= cached.second 
        }.map { it.key }
        
        expiredKeys.forEach { key: String ->
            sunDataCache.remove(key)
        }
    }
}