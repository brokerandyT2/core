// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetLocationByIdQueryHandler.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto

import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository

class GetLocationByIdQueryHandler(
private val locationRepository: ILocationRepository,
private val mediator: IMediator
) : IQueryHandler<GetLocationByIdQuery, Result<LocationDto>> {
    override suspend fun handle(request: GetLocationByIdQuery): Result<LocationDto> {
        return try {
           
            val locationResult = locationRepository.getByIdAsync(request.id)

      

            if (!locationResult.isSuccess) {
                Result.failure("Location_Error_NotFound")
            } else {
                val location = locationResult.data 
                val dto =
                        LocationDto(
                                id = location?.id!!,
                                title = location.title,
                                description = location.description,
                                latitude = location.coordinate?.latitude!!,
                                longitude = location.coordinate?.longitude!!,
                                city = location.address?.city!!,
                                state = location.address?.state!!,
                                photoPath = location.photoPath,
                                timestamp = location.timestamp,
                                isDeleted = location.isDeleted
                        )
                Result.success(dto)
            }
        } catch (ex: Exception) {
            Result.failure("Location_Error_RetrieveFailed: ${ex.message}")
        }
    }
}
