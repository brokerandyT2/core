// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/domain/services/ISunCalculatorService.kt
package com.x3squaredcircles.photography.domain.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Service for calculating sun, moon, and astronomical data
 * Kotlin equivalent of the .NET ISunCalculatorService
 */
interface ISunCalculatorService {
    
    // === SOLAR DATA ===

    /**
     * Gets the sunrise time for a specific date and location (returns UTC)
     */
    fun getSunrise(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the sunrise end time for a specific date and location (returns UTC)
     */
    fun getSunriseEnd(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the sunset start time for a specific date and location (returns UTC)
     */
    fun getSunsetStart(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the sunset time for a specific date and location (returns UTC)
     */
    fun getSunset(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the solar noon time for a specific date and location (returns UTC)
     */
    fun getSolarNoon(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the solar nadir time (opposite of solar noon) for a specific date and location (returns UTC)
     */
    fun getNadir(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the civil dawn time for a specific date and location (returns UTC)
     */
    fun getCivilDawn(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the civil dusk time for a specific date and location (returns UTC)
     */
    fun getCivilDusk(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the nautical dawn time for a specific date and location (returns UTC)
     */
    fun getNauticalDawn(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the nautical dusk time for a specific date and location (returns UTC)
     */
    fun getNauticalDusk(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the astronomical dawn time for a specific date and location (returns UTC)
     */
    fun getAstronomicalDawn(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the astronomical dusk time for a specific date and location (returns UTC)
     */
    fun getAstronomicalDusk(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the golden hour start time for a specific date and location (returns UTC)
     */
    fun getGoldenHourStart(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the golden hour end time for a specific date and location (returns UTC)
     */
    fun getGoldenHourEnd(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the blue hour start time for a specific date and location (returns UTC)
     */
    fun getBlueHourStart(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    /**
     * Gets the blue hour end time for a specific date and location (returns UTC)
     */
    fun getBlueHourEnd(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime

    // === SOLAR POSITION ===

    /**
     * Gets the solar azimuth for a specific date/time and location
     */
    fun getSolarAzimuth(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double

    /**
     * Gets the solar elevation for a specific date/time and location
     */
    fun getSolarElevation(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double

    /**
     * Gets the solar distance from Earth for a specific date/time
     */
    fun getSolarDistance(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double

    // === MOON DATA ===

    /**
     * Gets the moon rise time for a specific date and location (returns UTC)
     */
    fun getMoonrise(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime?

    /**
     * Gets the moon set time for a specific date and location (returns UTC)
     */
    fun getMoonset(date: Long, latitude: Double, longitude: Double, timezone: String): LocalDateTime?

    /**
     * Gets the moon phase for a specific date
     */
    fun getMoonPhase(date: Long): Double

    /**
     * Gets the moon illumination percentage for a specific date
     */
    fun getMoonIllumination(date: Long): Double

    /**
     * Gets the moon azimuth for a specific date/time and location
     */
    fun getMoonAzimuth(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double

    /**
     * Gets the moon elevation for a specific date/time and location
     */
    fun getMoonElevation(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double

    // === UTILITY METHODS ===

    /**
     * Determines if the sun is currently above the horizon
     */
    fun isSunUp(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Boolean

    /**
     * Determines if the moon is currently above the horizon
     */
    fun isMoonUp(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Boolean

    /**
     * Gets the equation of time for a specific date
     */
    fun getEquationOfTime(date: Long): Double

    /**
     * Gets the solar declination for a specific date
     */
    fun getSolarDeclination(date: Long): Double
}