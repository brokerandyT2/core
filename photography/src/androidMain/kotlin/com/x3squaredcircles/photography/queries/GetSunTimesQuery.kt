// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/queries/GetSunTimesQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IRequest
import com.x3squaredcircles.photography.domain.models.SunTimesDto
import kotlinx.datetime.LocalDate

data class GetSunTimesQuery(
    val latitude: Double,
    val longitude: Double,
    val date: LocalDate
) : IRequest<Result<SunTimesDto>>