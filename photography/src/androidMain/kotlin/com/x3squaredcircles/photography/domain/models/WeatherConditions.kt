// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/domain/models/WeatherConditions.kt
package com.x3squaredcircles.photography.domain.models

data class WeatherConditions(
    val cloudCover: Double = 0.0,
    val precipitation: Double = 0.0,
    val humidity: Double = 0.0,
    val visibility: Double = 0.0,
    val airQualityIndex: Int = 0,
    val windSpeed: Double = 0.0,
    val description: String = "",
    val uvIndex: Double = 0.0,
    val precipitationProbability: Double = 0.0
)