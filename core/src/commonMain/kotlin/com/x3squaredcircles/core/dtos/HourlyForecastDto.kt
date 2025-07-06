// core/src/commonMain/kotlin/com/x3squaredcircles/core/dtos/HourlyForecastDto.kt
package com.x3squaredcircles.core.dtos

data class HourlyForecastDto(
    val dateTime: Long,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val windDirection: Double,
    val windGust: Double?,
    val humidity: Int,
    val pressure: Int,
    val clouds: Int,
    val uvIndex: Double,
    val probabilityOfPrecipitation: Double,
    val visibility: Int,
    val dewPoint: Double
)