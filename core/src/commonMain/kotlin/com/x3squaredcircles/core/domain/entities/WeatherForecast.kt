// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/WeatherForecast.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.core.domain.valueobjects.WindInfo

/**
 * Individual weather forecast for a single day
 */
class WeatherForecast private constructor() : Entity() {
    
    private var _weatherId: Int = 0
    private var _date: Long = 0L
    private var _sunrise: Long = 0L
    private var _sunset: Long = 0L
    private var _temperature: Double = 0.0
    private var _minTemperature: Double = 0.0
    private var _maxTemperature: Double = 0.0
    private var _description: String = ""
    private var _icon: String = ""
    private var _wind: WindInfo? = null
    private var _humidity: Int = 0
    private var _pressure: Int = 0
    private var _clouds: Int = 0
    private var _uvIndex: Double = 0.0
    private var _precipitation: Double? = null
    private var _moonRise: Long? = null
    private var _moonSet: Long? = null
    private var _moonPhase: Double = 0.0
    
    val weatherId: Int get() = _weatherId
    val date: Long get() = _date
    val sunrise: Long get() = _sunrise
    val sunset: Long get() = _sunset
    val temperature: Double get() = _temperature
    val minTemperature: Double get() = _minTemperature
    val maxTemperature: Double get() = _maxTemperature
    val description: String get() = _description
    val icon: String get() = _icon
    val wind: WindInfo? get() = _wind
    val humidity: Int get() = _humidity
    val pressure: Int get() = _pressure
    val clouds: Int get() = _clouds
    val uvIndex: Double get() = _uvIndex
    val precipitation: Double? get() = _precipitation
    val moonRise: Long? get() = _moonRise
    val moonSet: Long? get() = _moonSet
    val moonPhase: Double get() = _moonPhase
    
    constructor(
        weatherId: Int,
        date: Long,
        sunrise: Long,
        sunset: Long,
        temperature: Double,
        minTemperature: Double,
        maxTemperature: Double,
        description: String,
        icon: String,
        wind: WindInfo,
        humidity: Int,
        pressure: Int,
        clouds: Int,
        uvIndex: Double
    ) : this() {
        _weatherId = weatherId
        _date = date
        _sunrise = sunrise
        _sunset = sunset
        _temperature = temperature
        _minTemperature = minTemperature
        _maxTemperature = maxTemperature
        _description = description
        _icon = icon
        _wind = wind
        _humidity = validatePercentage(humidity, "humidity")
        _pressure = pressure
        _clouds = validatePercentage(clouds, "clouds")
        _uvIndex = uvIndex
    }
    
    fun setMoonData(moonRise: Long?, moonSet: Long?, moonPhase: Double) {
        _moonRise = moonRise
        _moonSet = moonSet
        _moonPhase = moonPhase.coerceIn(0.0, 1.0)
    }
    
    fun setPrecipitation(precipitation: Double) {
        _precipitation = maxOf(0.0, precipitation)
    }
    
    private fun validatePercentage(value: Int, paramName: String): Int {
        require(value in 0..100) { "$paramName must be between 0 and 100" }
        return value
    }
    
    /**
     * Gets moon phase description
     */
    fun getMoonPhaseDescription(): String {
        return when (_moonPhase) {
            in 0.0..0.03 -> "New Moon"
            in 0.03..0.22 -> "Waxing Crescent"
            in 0.22..0.28 -> "First Quarter"
            in 0.28..0.47 -> "Waxing Gibbous"
            in 0.47..0.53 -> "Full Moon"
            in 0.53..0.72 -> "Waning Gibbous"
            in 0.72..0.78 -> "Last Quarter"
            in 0.78..0.97 -> "Waning Crescent"
            else -> "New Moon"
        }
    }
}