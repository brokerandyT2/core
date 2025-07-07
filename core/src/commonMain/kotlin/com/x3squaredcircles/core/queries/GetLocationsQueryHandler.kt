// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetLocationsQueryHandler.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationListDto
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.core.models.PagedList

class GetLocationsQueryHandler(
    private val locationRepository: ILocationRepository
) : IQueryHandler<GetLocationsQuery, Result<PagedList<LocationListDto>>> {

    override suspend fun handle(request: GetLocationsQuery): Result<PagedList<LocationListDto>> {
        return try {
            // Push all filtering and pagination to database level
            val pagedLocationsResult = locationRepository.getPagedAsync(
                pageNumber = request.pageNumber,
                pageSize = request.pageSize,
                searchTerm = request.searchTerm,
                includeDeleted = request.includeDeleted
            )

            if (!pagedLocationsResult.isSuccess) {
                val errorMsg = when (pagedLocationsResult) {
                    is Result.Failure -> pagedLocationsResult.errorMessage
                    else -> "Location_Error_ListRetrieveFailed"
                }
                return Result.failure(errorMsg)
            }

            val pagedLocations = pagedLocationsResult.data
            if (pagedLocations == null) {
                return Result.failure("Location_Error_ListRetrieveFailed")
            }

            // Map domain entities to DTOs
            val locationDtos = pagedLocations.items.map { location ->
                LocationListDto(
                    id = location.id,
                    title = location.title,
                    city = location.address?.city ?: "",
                    state = location.address?.state ?: "",
                    photoPath = location.photoPath,
                    timestamp = location.timestamp,
                    isDeleted = location.isDeleted,
                    latitude = location.coordinate?.latitude ?: 0.0,
                    longitude = location.coordinate?.longitude ?: 0.0
                )
            }

            val pagedListDto = PagedList(
                items = locationDtos,
                pageNumber = pagedLocations.pageNumber,
                pageSize = pagedLocations.pageSize,
                totalCount = pagedLocations.totalCount
            )

            Result.success(pagedListDto)
        } catch (ex: Exception) {
            Result.failure("Location_Error_ListRetrieveFailedWithException: ${ex.message}")
        }
    }
}