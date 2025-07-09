// com/x3squaredcircles/photography/infrastructure/services/SunService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.application.services.ISunService
import com.x3squaredcircles.photography.domain.models.SunPositionDto
import com.x3squaredcircles.photography.domain.models.SunTimesDto
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class SunService(
    private val sunCalculatorService: ISunCalculatorService
) : ISunService {
    
    private val sunDataCache = mutableMapOf<String, Pair<Any, Long>>()
    private val cacheTimeout = 15 * 60 * 1000L // 15 minutes in milliseconds
    private val cacheMutex = Mutex()
    
    private suspend fun <T> getCachedOrCalculate(cacheKey: String, calculation: suspend () -> T): T {
        return cacheMutex.withLock {
            val cached = sunDataCache[cacheKey]
            if (cached != null && System.currentTimeMillis() < cached.second) {
                @Suppress("UNCHECKED_CAST")
                cached.first as T
            } else {
                val result = calculation()
                sunDataCache[cacheKey] = Pair(result as Any, System.currentTimeMillis() + cacheTimeout)
                result
            }
        }
    }
    
    override suspend fun getSunPositionAsync(
        latitude: Double,
        longitude: Double,
        dateTime: Long,
        cancellationToken: Job
    ): Result<SunPositionDto> {
        if (cancellationToken.isCancelled) {
            return Result.failure("Operation was cancelled")
        }
        
        return try {
            val cacheKey = "position_${dateTime}_${latitude}_${longitude}"
            
            val result = getCachedOrCalculate(cacheKey) {
                withContext(Dispatchers.Default) {
                    val timezone = "UTC" // Use UTC for calculations
                    val azimuth = sunCalculatorService.getSolarAzimuth(dateTime, latitude, longitude, timezone)
                    val elevation = sunCalculatorService.getSolarElevation(dateTime, latitude, longitude, timezone)
                    
                    SunPositionDto(
                        azimuth = azimuth,
                        elevation = elevation,
                        dateTime = dateTime,
                        latitude = latitude,
                        longitude = longitude,
                        distance = 1.0
                    )
                }
            }
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure("Error calculating sun position: ${e.message}")
        }
    }
    
    override suspend fun getSunTimesAsync(
        latitude: Double,
        longitude: Double,
        date: Long,
        cancellationToken: Job
    ): Result<SunTimesDto> {
        if (cancellationToken.isCancelled) {
            return Result.failure("Operation was cancelled")
        }
        
        return try {
            // Use day-based cache key (strip time component for daily calculations)
            val dayTimestamp = (date / (24 * 60 * 60 * 1000L)) * (24 * 60 * 60 * 1000L)
            val cacheKey = "times_${dayTimestamp}_${latitude}_${longitude}"
            
            val result = getCachedOrCalculate(cacheKey) {
                withContext(Dispatchers.Default) {
                    val timezone = "UTC" // Use UTC for calculations
                    
                    val sunrise = sunCalculatorService.getSunrise(date, latitude, longitude, timezone)
                    val sunset = sunCalculatorService.getSunset(date, latitude, longitude, timezone)
                    val solarNoon = sunCalculatorService.getSolarNoon(date, latitude, longitude, timezone)
                    val astronomicalDawn = sunCalculatorService.getAstronomicalDawn(date, latitude, longitude, timezone)
                    val astronomicalDusk = sunCalculatorService.getAstronomicalDusk(date, latitude, longitude, timezone)
                    val nauticalDawn = sunCalculatorService.getNauticalDawn(date, latitude, longitude, timezone)
                    val nauticalDusk = sunCalculatorService.getNauticalDusk(date, latitude, longitude, timezone)
                    val civilDawn = sunCalculatorService.getCivilDawn(date, latitude, longitude, timezone)
                    val civilDusk = sunCalculatorService.getCivilDusk(date, latitude, longitude, timezone)
                    val goldenHourStart = sunCalculatorService.getGoldenHourStart(date, latitude, longitude, timezone)
                    val goldenHourEnd = sunCalculatorService.getGoldenHourEnd(date, latitude, longitude, timezone)
                    
                    SunTimesDto(
                        date = date,
                        latitude = latitude,
                        longitude = longitude,
                        sunrise = sunrise,
                        sunset = sunset,
                        solarNoon = solarNoon,
                        astronomicalDawn = astronomicalDawn,
                        astronomicalDusk = astronomicalDusk,
                        nauticalDawn = nauticalDawn,
                        nauticalDusk = nauticalDusk,
                        civilDawn = civilDawn,
                        civilDusk = civilDusk,
                        goldenHourMorningStart = goldenHourEnd,
                        goldenHourMorningEnd = sunrise,
                        goldenHourEveningStart = goldenHourStart,
                        goldenHourEveningEnd = sunset
                    )
                }
            }
            
            // Use longer timeout for daily sun times (they don't change as frequently)
            val dailyCacheTimeout = 2 * 60 * 60 * 1000L // 2 hours
            cacheMutex.withLock {
                sunDataCache[cacheKey] = Pair(result, System.currentTimeMillis() + dailyCacheTimeout)
            }
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure("Error calculating sun times: ${e.message}")
        }
    }
}