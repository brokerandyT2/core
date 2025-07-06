// core\src\commonMain\kotlin\com\x3squaredcircles\core\queries\GetLocationsQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.models.PagedList
import com.x3squaredcircles.core.dtos.LocationListDto
import com.x3squaredcircles.core.mediator.IQuery
data class GetLocationsQuery(
val pageNumber: Int = 1,
val pageSize: Int = 10,
val searchTerm: String? = null,
val includeDeleted: Boolean = false
) : IQuery<Result<PagedList<LocationListDto>>>