// core\src\commonMain\kotlin\com\x3squaredcircles\core\queries\GetWeatherForecastQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.WeatherForecastDto
import com.x3squaredcircles.core.mediator.IQuery
data class GetWeatherForecastQuery(
val latitude: Double,
val longitude: Double,
val days: Int = 7
) : IQuery<Result<WeatherForecastDto>>