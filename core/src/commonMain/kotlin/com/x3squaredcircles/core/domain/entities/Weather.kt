// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Weather.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.AggregateRoot
import com.x3squaredcircles.core.domain.events.WeatherUpdatedEvent
import com.x3squaredcircles.core.domain.valueobjects.Coordinate

/**
 * Weather aggregate root containing weather data for a location
 */
class Weather private constructor() : AggregateRoot() {
    
    private val _forecasts = mutableListOf<WeatherForecast>()
    private val _hourlyForecasts = mutableListOf<HourlyForecast>()
    private var _coordinate: Coordinate? = null
    private var _lastUpdate: Long = 0L
    private var _timezone: String = ""
    private var _timezoneOffset: Int = 0
    private var _locationId: Int = 0
    
    val locationId: Int get() = _locationId
    val coordinate: Coordinate? get() = _coordinate
    val lastUpdate: Long get() = _lastUpdate
    val timezone: String get() = _timezone
    val timezoneOffset: Int get() = _timezoneOffset
    val forecasts: List<WeatherForecast> get() = _forecasts.toList()
    val hourlyForecasts: List<HourlyForecast> get() = _hourlyForecasts.toList()
    
    constructor(locationId: Int, coordinate: Coordinate, timezone: String, timezoneOffset: Int) : this() {
        _locationId = locationId
        _coordinate = coordinate
        _timezone = timezone
        _timezoneOffset = timezoneOffset
        _lastUpdate = System.currentTimeMillis()
    }
    
    fun updateForecasts(forecasts: List<WeatherForecast>) {
        _forecasts.clear()
        _forecasts.addAll(forecasts.take(7)) // Limit to 7-day forecast
        _lastUpdate = System.currentTimeMillis()
        
        addDomainEvent(WeatherUpdatedEvent(locationId, lastUpdate))
    }
    
    fun updateHourlyForecasts(hourlyForecasts: List<HourlyForecast>) {
        _hourlyForecasts.clear()
        _hourlyForecasts.addAll(hourlyForecasts.take(48)) // Limit to 48-hour forecast
        _lastUpdate = System.currentTimeMillis()
        
        addDomainEvent(WeatherUpdatedEvent(locationId, lastUpdate))
    }
    
    fun getForecastForDate(date: Long): WeatherForecast? {
        val targetDateOnly = date / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)
        return _forecasts.firstOrNull { forecast ->
            val forecastDateOnly = forecast.date / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)
            forecastDateOnly == targetDateOnly
        }
    }
    
    fun getCurrentForecast(): WeatherForecast? {
        return getForecastForDate(System.currentTimeMillis())
    }
    
    fun getHourlyForecastsForDate(date: Long): List<HourlyForecast> {
        val targetDateOnly = date / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)
        return _hourlyForecasts.filter { hourlyForecast ->
            val hourlyDateOnly = hourlyForecast.dateTime / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)
            hourlyDateOnly == targetDateOnly
        }
    }
    
    fun getHourlyForecastsForRange(startTime: Long, endTime: Long): List<HourlyForecast> {
        return _hourlyForecasts.filter { h -> h.dateTime in startTime..endTime }
    }
    
    fun getCurrentHourlyForecast(): HourlyForecast? {
        val currentTime = System.currentTimeMillis()
        val currentHour = (currentTime / (60 * 60 * 1000)) * (60 * 60 * 1000)
        return _hourlyForecasts.firstOrNull { h -> h.dateTime >= currentHour }
    }
}