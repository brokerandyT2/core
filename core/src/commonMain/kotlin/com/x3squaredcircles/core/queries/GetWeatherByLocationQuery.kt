// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetWeatherByLocationQuery.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.WeatherDataDto
import com.x3squaredcircles.core.mediator.IQuery

data class GetWeatherByLocationQuery(val locationId: Int) : IQuery<Result<WeatherDataDto>>
