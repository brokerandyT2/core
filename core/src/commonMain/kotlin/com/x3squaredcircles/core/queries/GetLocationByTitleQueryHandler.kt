// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetLocationByTitleQueryHandler.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.mediator.IQueryHandler

class GetLocationByTitleQueryHandler(
    private val locationRepository: ILocationRepository
) : IQueryHandler<GetLocationByTitleQuery, Result<LocationDto>> {

    override suspend fun handle(request: GetLocationByTitleQuery): Result<LocationDto> {
        return try {
            val locationResult = locationRepository.getByTitleAsync(request.title)
            
            if (!locationResult.isSuccess || locationResult.data == null) {
                return Result.failure("Location_Error_TitleNotFound: ${request.title}")
            }

            val location = locationResult.data!!
            val locationDto = LocationDto(
                id = location.id,
                title = location.title,
                description = location.description,
                latitude = location.coordinate?.latitude ?: 0.0,
                longitude = location.coordinate?.longitude ?: 0.0,
                city = location.address?.city ?: "",
                state = location.address?.state ?: "",
                photoPath = location.photoPath,
                timestamp = location.timestamp,
                isDeleted = location.isDeleted
            )

            Result.success(locationDto)
        } catch (ex: Exception) {
            Result.failure("Location_Error_RetrieveFailed: ${ex.message}")
        }
    }
}