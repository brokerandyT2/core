// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/external/models/WeatherApiResponse.kt
package com.x3squaredcircles.core.infrastructure.external.models

import com.x3squaredcircles.core.dtos.DailyForecastDto
import com.x3squaredcircles.core.dtos.HourlyForecastDto


data class WeatherApiResponse(
    val timezone: String,
    val timezoneOffset: Int,
    val dailyForecasts: List<DailyForecastDto>,
    val hourlyForecasts: List<HourlyForecastDto>
)