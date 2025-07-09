// com/x3squaredcircles/photography/domain/services/ISunCalculatorService.kt
package com.x3squaredcircles.photography.domain.services

import 

/**
 * Service for calculating sun, moon, and astronomical data using Astronomy Engine
 * @see https://github.com/cosinekitty/astronomy
 */
interface ISunCalculatorService {
    
    // === SOLAR DATA ===
    
    /**
     * Gets the sunrise time for a specific date and location (returns UTC)
     */
    fun getSunrise(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the sunrise end time for a specific date and location (returns UTC)
     */
    fun getSunriseEnd(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the sunset start time for a specific date and location (returns UTC)
     */
    fun getSunsetStart(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the sunset time for a specific date and location (returns UTC)
     */
    fun getSunset(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the solar noon time for a specific date and location (returns UTC)
     */
    fun getSolarNoon(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the solar nadir time (opposite of solar noon) for a specific date and location (returns UTC)
     */
    fun getNadir(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the civil dawn time for a specific date and location (returns UTC)
     */
    fun getCivilDawn(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the civil dusk time for a specific date and location (returns UTC)
     */
    fun getCivilDusk(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the nautical dawn time for a specific date and location (returns UTC)
     */
    fun getNauticalDawn(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the nautical dusk time for a specific date and location (returns UTC)
     */
    fun getNauticalDusk(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the astronomical dawn time for a specific date and location (returns UTC)
     */
    fun getAstronomicalDawn(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the astronomical dusk time for a specific date and location (returns UTC)
     */
    fun getAstronomicalDusk(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the golden hour start time (evening) for a specific date and location (returns UTC)
     */
    fun getGoldenHourStart(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the golden hour end time (morning) for a specific date and location (returns UTC)
     */
    fun getGoldenHourEnd(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the blue hour start time (evening) for a specific date and location (returns UTC)
     */
    fun getBlueHourStart(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the blue hour end time (morning) for a specific date and location (returns UTC)
     */
    fun getBlueHourEnd(date: Long, latitude: Double, longitude: Double, timezone: String): Long
    
    /**
     * Gets the solar azimuth (compass direction) for a specific date/time and location
     */
    fun getSolarAzimuth(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the solar elevation (altitude above horizon) for a specific date/time and location
     */
    fun getSolarElevation(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double
    
    // === LUNAR DATA ===
    
    /**
     * Gets the moon rise time for a specific date and location (returns UTC)
     */
    fun getMoonrise(date: Long, latitude: Double, longitude: Double, timezone: String): Long?
    
    /**
     * Gets the moon set time for a specific date and location (returns UTC)
     */
    fun getMoonset(date: Long, latitude: Double, longitude: Double, timezone: String): Long?
    
    /**
     * Gets the moon phase for a specific date and location
     */
    fun getMoonPhase(date: Long, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the moon illumination percentage for a specific date and location
     */
    fun getMoonIllumination(date: Long, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the moon azimuth (compass direction) for a specific date/time and location
     */
    fun getMoonAzimuth(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Gets the moon elevation (altitude above horizon) for a specific date/time and location
     */
    fun getMoonElevation(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double
    
    /**
     * Checks if the moon is above the horizon at a specific date/time and location
     */
    fun isMoonUp(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Boolean
}