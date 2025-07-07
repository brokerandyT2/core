// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetNearbyLocationsQueryHandler.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationListDto
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.mediator.IQueryHandler

class GetNearbyLocationsQueryHandler(
    private val locationRepository: ILocationRepository
) : IQueryHandler<GetNearbyLocationsQuery, Result<List<LocationListDto>>> {

    override suspend fun handle(request: GetNearbyLocationsQuery): Result<List<LocationListDto>> {
        return try {
            val result = locationRepository.getNearbyAsync(
                request.latitude,
                request.longitude,
                request.distanceKm
            )

     if (!result.isSuccess) {
                val errorMsg = when (result) {
                    is Result.Failure -> result.errorMessage
                    else -> "Location_Error_NearbyRetrieveFailed"
                }
                return Result.failure(errorMsg)
            }

            val locations = result.data
            if (locations == null) {
                return Result.success(emptyList())
            }

            val locationDtos = locations.map { location ->
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

            Result.success(locationDtos)
        } catch (ex: Exception) {
            Result.failure("Location_Error_NearbyRetrieveFailed: ${ex.message}")
        }
    }
}