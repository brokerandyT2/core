// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/SaveLocationCommandHandler.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Location
import com.x3squaredcircles.core.domain.valueobjects.Address
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator

class SaveLocationCommandHandler(
        private val locationRepository: ILocationRepository,
        private val mediator: IMediator
) : ICommandHandler<SaveLocationCommand, Result<LocationDto>> {
    override suspend fun handle(request: SaveLocationCommand): Result<LocationDto> {
        return try {
            val location: Location

            if (request.id != null) {
                val existingLocationResult = locationRepository.getByIdAsync(request.id)
                if (!existingLocationResult.isSuccess || existingLocationResult.data == null) {
                    return Result.failure("Location_Error_NotFound")
                }

                location = existingLocationResult.data as Location
                location.updateDetails(request.title, request.description)

                val newCoordinate = Coordinate.create(request.latitude, request.longitude)
                location.updateCoordinate(newCoordinate)

                if (!request.photoPath.isNullOrEmpty()) {
                    location.attachPhoto(request.photoPath)
                }

                val updateResult = locationRepository.updateAsync(location)
                if (!updateResult.isSuccess) {
                    return Result.failure("Location_Error_UpdateFailed")
                }
            } else {
                val coordinate = Coordinate.create(request.latitude, request.longitude)
                val address = Address(request.city, request.state)

                location = Location(request.title, request.description, coordinate, address)

                if (!request.photoPath.isNullOrEmpty()) {
                    location.attachPhoto(request.photoPath)
                }

                val createResult = locationRepository.createAsync(location)
                if (!createResult.isSuccess) {
                    return Result.failure("Location_Error_CreateFailed")
                }
            }

            val dto =
                    LocationDto(
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

            Result.success(dto)
        } catch (ex: Exception) {
            Result.failure("Location_Error_SaveFailed: ${ex.message}")
        }
    }
}
