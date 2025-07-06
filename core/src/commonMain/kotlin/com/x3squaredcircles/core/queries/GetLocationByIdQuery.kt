// core\src\commonMain\kotlin\com\x3squaredcircles\core\queries\GetLocationByIdQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.mediator.IQuery
data class GetLocationByIdQuery(
val id: Int
) : IQuery<Result<LocationDto>>