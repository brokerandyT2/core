// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/rules/WeatherValidationRules.kt
package com.x3squaredcircles.core.domain.rules

import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.domain.entities.WeatherForecast

/**
 * Business rules for weather validation
 */
object WeatherValidationRules {
    
    private const val MAX_FORECAST_DAYS = 7
    private const val MIN_TEMPERATURE = -273.15 // Absolute zero in Celsius
    private const val MAX_TEMPERATURE = 70.0 // Reasonable max temperature in Celsius
    
    /**
     * Validates the specified Weather object and identifies any validation errors.
     */
    fun isValid(weather: Weather?): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        
        if (weather == null) {
            errors.add("Weather cannot be null")
            return Pair(false, errors)
        }
        
        if (weather.locationId <= 0) {
            errors.add("Weather must be associated with a valid location")
        }
        
        if (weather.coordinate == null) {
            errors.add("Weather coordinates are required")
        }
        
        if (weather.timezone.isBlank()) {
            errors.add("Weather timezone is required")
        }
        
        if (weather.forecasts.size > MAX_FORECAST_DAYS) {
            errors.add("Weather cannot have more than $MAX_FORECAST_DAYS daily forecasts")
        }
        
        // Validate each forecast
        weather.forecasts.forEach { forecast ->
            validateForecast(forecast, errors)
        }
        
        return Pair(errors.isEmpty(), errors)
    }
    
    /**
     * Validates the specified weather forecast and collects any validation errors.
     */
    private fun validateForecast(forecast: WeatherForecast, errors: MutableList<String>) {
        val dateString = formatDate(forecast.date)
        
        if (forecast.temperature < MIN_TEMPERATURE || forecast.temperature > MAX_TEMPERATURE) {
            errors.add("Invalid temperature for $dateString")
        }
        
        if (forecast.minTemperature > forecast.maxTemperature) {
            errors.add("Min temperature cannot exceed max temperature for $dateString")
        }
        
        if (forecast.humidity !in 0..100) {
            errors.add("Invalid humidity percentage for $dateString")
        }
        
        if (forecast.clouds !in 0..100) {
            errors.add("Invalid cloud coverage percentage for $dateString")
        }
        
        if (forecast.uvIndex < 0 || forecast.uvIndex > 15) {
            errors.add("Invalid UV index for $dateString")
        }
        
        if (forecast.moonPhase < 0 || forecast.moonPhase > 1) {
            errors.add("Invalid moon phase for $dateString")
        }
    }
    
    /**
     * Determines whether the specified weather data is considered stale based on the given maximum age.
     */
    fun isStale(weather: Weather, maxAgeMillis: Long): Boolean {
        return System.currentTimeMillis() - weather.lastUpdate > maxAgeMillis
    }
    
    private fun formatDate(timestamp: Long): String {
        // Simple date formatting - you may want to use a proper date formatter
        return "Date(${timestamp})"
    }
}