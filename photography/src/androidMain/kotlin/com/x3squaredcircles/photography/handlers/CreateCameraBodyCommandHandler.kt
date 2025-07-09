// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/handlers/CreateCameraBodyCommandHandler.kt
package com.x3squaredcircles.photography.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.CreateCameraBodyCommand
import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photography.repositories.ICameraBodyRepository
import kotlinx.coroutines.Job

class CreateCameraBodyCommandHandler(
    private val cameraBodyRepository: ICameraBodyRepository
) : ICommandHandler<CreateCameraBodyCommand, Result<CameraBodyDto>> {

    override suspend fun handle(request: CreateCameraBodyCommand): Result<CameraBodyDto> {
        return try {
            // Check for fuzzy duplicate
            val existingResult = cameraBodyRepository.searchByNameAsync(request.name)
            if (existingResult.isSuccess && (existingResult.data?.isNotEmpty() == true)) {
                return Result.failure("Camera with similar name already exists")
            }

            // Create the camera body
            val cameraBody = CameraBody(
                name = request.name,
                sensorType = request.sensorType,
                sensorWidth = request.sensorWidth,
                sensorHeight = request.sensorHeight,
                mountType = request.mountType,
                isUserCreated = request.isUserCreated
            )

            val createResult = cameraBodyRepository.createAsync(cameraBody)

            if (!createResult.isSuccess) {
                val errorMsg = when (createResult) {
                    is Result.Failure -> createResult.errorMessage
                    else -> "Error creating camera"
                }
                return Result.failure(errorMsg)
            }

            val createdCamera = createResult.data!!
            val dto = CameraBodyDto(
                id = createdCamera.id,
                name = createdCamera.name,
                sensorType = createdCamera.sensorType,
                sensorWidth = createdCamera.sensorWidth,
                sensorHeight = createdCamera.sensorHeight,
                mountType = createdCamera.mountType,
                isUserCreated = createdCamera.isUserCreated,
                dateAdded = createdCamera.dateAdded,
                displayName = createdCamera.getDisplayName()
            )

            Result.success(dto)
        } catch (e: Exception) {
            Result.failure("Error creating camera body: ${e.message}")
        }
    }
}