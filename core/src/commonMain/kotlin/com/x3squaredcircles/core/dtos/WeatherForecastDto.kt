// core\src\commonMain\kotlin\com\x3squaredcircles\core\dtos\WeatherForecastDto.kt
package com.x3squaredcircles.core.dtos
data class WeatherForecastDto(
val weatherId: Int,
val lastUpdate: Long,
val timezone: String,
val timezoneOffset: Int,
val dailyForecasts: List<DailyForecastDto>
)
data class DailyForecastDto(
val date: Long,
val sunrise: Long,
val sunset: Long,
val temperature: Double,
val minTemperature: Double,
val maxTemperature: Double,
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
val moonRise: Long?,
val moonSet: Long?,
val moonPhase: Double
)