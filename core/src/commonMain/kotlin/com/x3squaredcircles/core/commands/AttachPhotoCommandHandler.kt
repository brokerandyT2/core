// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/AttachPhotoCommandHandler.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator

class AttachPhotoCommandHandler(
    private val locationRepository: ILocationRepository,
    private val mediator: IMediator
) : ICommandHandler<AttachPhotoCommand, Result<LocationDto>> {

    override suspend fun handle(request: AttachPhotoCommand): Result<LocationDto> {
        return try {
            val locationResult = locationRepository.getByIdAsync(request.locationId)

            if (!locationResult.isSuccess || locationResult.data == null) {
                return Result.failure("Location_Error_NotFound")
            }

            val location = locationResult.data!!
            location.attachPhoto(request.photoPath)

            val updateResult = locationRepository.updateAsync(location)
            if (!updateResult.isSuccess) {
                return Result.failure("Location_Error_UpdateFailed")
            }

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
            when {
                ex.message?.contains("INVALID_PHOTO_PATH") == true -> 
                    Result.failure("Location_Error_PhotoPathInvalid")
                else -> 
                    Result.failure("Location_Error_AttachPhotoFailed: ${ex.message}")
            }
        }
    }
}