// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetNearbyLocationsQuery.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationListDto
import com.x3squaredcircles.core.mediator.IQuery

data class GetNearbyLocationsQuery(
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double = 10.0
) : IQuery<Result<List<LocationListDto>>>