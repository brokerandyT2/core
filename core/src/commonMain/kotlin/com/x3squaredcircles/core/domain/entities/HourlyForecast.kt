// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/HourlyForecast.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.core.domain.valueobjects.WindInfo

/**
 * Individual weather forecast for a single hour
 */
class HourlyForecast private constructor() : Entity() {
    
    private var _weatherId: Int = 0
    private var _dateTime: Long = 0L
    private var _temperature: Double = 0.0
    private var _feelsLike: Double = 0.0
    private var _description: String = ""
    private var _icon: String = ""
    private var _wind: WindInfo? = null
    private var _humidity: Int = 0
    private var _pressure: Int = 0
    private var _clouds: Int = 0
    private var _uvIndex: Double = 0.0
    private var _probabilityOfPrecipitation: Double = 0.0
    private var _visibility: Int = 0
    private var _dewPoint: Double = 0.0
    
    val weatherId: Int get() = _weatherId
    val dateTime: Long get() = _dateTime
    val temperature: Double get() = _temperature
    val feelsLike: Double get() = _feelsLike
    val description: String get() = _description
    val icon: String get() = _icon
    val wind: WindInfo? get() = _wind
    val humidity: Int get() = _humidity
    val pressure: Int get() = _pressure
    val clouds: Int get() = _clouds
    val uvIndex: Double get() = _uvIndex
    val probabilityOfPrecipitation: Double get() = _probabilityOfPrecipitation
    val visibility: Int get() = _visibility
    val dewPoint: Double get() = _dewPoint
    
    constructor(
        weatherId: Int,
        dateTime: Long,
        temperature: Double,
        feelsLike: Double,
        description: String,
        icon: String,
        wind: WindInfo,
        humidity: Int,
        pressure: Int,
        clouds: Int,
        uvIndex: Double,
        probabilityOfPrecipitation: Double,
        visibility: Int,
        dewPoint: Double
    ) : this() {
        _weatherId = weatherId
        _dateTime = dateTime
        _temperature = temperature
        _feelsLike = feelsLike
        _description = description
        _icon = icon
        _wind = wind
        _humidity = validatePercentage(humidity, "humidity")
        _pressure = pressure
        _clouds = validatePercentage(clouds, "clouds")
        _uvIndex = maxOf(0.0, uvIndex)
        _probabilityOfPrecipitation = validateProbability(probabilityOfPrecipitation, "probabilityOfPrecipitation")
        _visibility = maxOf(0, visibility)
        _dewPoint = dewPoint
    }
    
    private fun validatePercentage(value: Int, paramName: String): Int {
        require(value in 0..100) { "$paramName must be between 0 and 100" }
        return value
    }
    
    private fun validateProbability(value: Double, paramName: String): Double {
        require(value in 0.0..1.0) { "$paramName must be between 0 and 1" }
        return value
    }
}