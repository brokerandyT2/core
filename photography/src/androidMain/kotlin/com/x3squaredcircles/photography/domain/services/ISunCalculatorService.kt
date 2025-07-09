// com/x3squaredcircles/photography/domain/services/ISunCalculatorService.kt
package com.x3squaredcircles.photography.domain.services

import kotlinx.datetime.LocalDateTime

/**
 * Service for calculating sun, moon, and astronomical data using Astronomy Engine
 * @see https://github.com/cosinekitty/astronomy
 */
interface ISunCalculatorService {
    
    // === SOLAR DATA ===
    
    /**
     * Gets the sunrise time for a specific date and location (returns UTC)
     */
    fun getSunrise(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the sunrise end time for a specific date and location (returns UTC)
     */
    fun getSunriseEnd(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the sunset start time for a specific date and location (returns UTC)
     */
    fun getSunsetStart(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the sunset time for a specific date and location (returns UTC)
     */
    fun getSunset(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the solar noon time for a specific date and location (returns UTC)
     */
    fun getSolarNoon(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the solar nadir time (opposite of solar noon) for a specific date and location (returns UTC)
     */
    fun getNadir(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the civil dawn time for a specific date and location (returns UTC)
     */
    fun getCivilDawn(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the civil dusk time for a specific date and location (returns UTC)
     */
    fun getCivilDusk(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the nautical dawn time for a specific date and location (returns UTC)
     */
    fun getNauticalDawn(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the nautical dusk time for a specific date and location (returns UTC)
     */
    fun getNauticalDusk(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the astronomical dawn time for a specific date and location (returns UTC)
     */
    fun getAstronomicalDawn(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the astronomical dusk time for a specific date and location (returns UTC)
     */
    fun getAstronomicalDusk(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the golden hour start time (evening) for a specific date and location (returns UTC)
     */
    fun getGoldenHourStart(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the golden hour end time (morning) for a specific date and location (returns UTC)
     */
    fun getGoldenHourEnd(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the blue hour start time (evening) for a specific date and location (returns UTC)
     */
    fun getBlueHourStart(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the blue hour end time (morning) for a specific date and location (returns UTC)
     */
    fun getBlueHourEnd(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime
    
    /**
     * Gets the solar azimuth (compass direction) for a specific date/time and location
     */
    fun getSolarAzimuth(dateTime: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the solar elevation (altitude above horizon) for a specific date/time and location
     */
    fun getSolarElevation(dateTime: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Double
    
    // === LUNAR DATA ===
    
    /**
     * Gets the moon rise time for a specific date and location (returns UTC)
     */
    fun getMoonrise(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime?
    
    /**
     * Gets the moon set time for a specific date and location (returns UTC)
     */
    fun getMoonset(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): LocalDateTime?
    
    /**
     * Gets the moon phase for a specific date and location
     */
    fun getMoonPhase(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the moon illumination percentage for a specific date and location
     */
    fun getMoonIllumination(date: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the moon azimuth (compass direction) for a specific date/time and location
     */
    fun getMoonAzimuth(dateTime: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the moon elevation (altitude above horizon) for a specific date/time and location
     */
    fun getMoonElevation(dateTime: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Checks if the moon is above the horizon at a specific date/time and location
     */
    fun isMoonUp(dateTime: LocalDateTime, latitude: Double, longitude: Double, timezone: String): Boolean
}