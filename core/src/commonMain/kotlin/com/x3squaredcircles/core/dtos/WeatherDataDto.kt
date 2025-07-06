// core\src\commonMain\kotlin\com\x3squaredcircles\core\dtos\WeatherDataDto.kt
package com.x3squaredcircles.core.dtos
data class WeatherDataDto(
val id: Int,
val locationId: Int,
val latitude: Double,
val longitude: Double,
val timezone: String,
val timezoneOffset: Int,
val lastUpdate: Long,
val temperature: Double,
val description: String,
val icon: String,
val windSpeed: Double,
val windDirection: Double,
val windGust: Double?,
val humidity: Int,
val pressure: Int,
val clouds: Int,
val uvIndex: Double,
val precipitation: Double?,
val sunrise: Long,
val sunset: Long,
val moonRise: Long?,
val moonSet: Long?,
val moonPhase: Double,
val minimumTemp: Double,
val maximumTemp: Double
)