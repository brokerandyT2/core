// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/handlers/CreateLensCommandHandler.kt
package com.x3squaredcircles.photography.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.CreateLensCommand
import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility
import com.x3squaredcircles.photography.dtos.CreateLensResultDto
import com.x3squaredcircles.photography.dtos.LensDto
import com.x3squaredcircles.photography.repositories.ILensRepository
import com.x3squaredcircles.photography.repositories.ILensCameraCompatibilityRepository

class CreateLensCommandHandler(
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository
) : ICommandHandler<CreateLensCommand, Result<CreateLensResultDto>> {

    override suspend fun handle(request: CreateLensCommand): Result<CreateLensResultDto> {
        return try {
            // Create the lens
            val lens = Lens(
                minMM = request.minMM,
                maxMM = request.maxMM,
                minFStop = request.minFStop,
                maxFStop = request.maxFStop,
                isUserCreated = request.isUserCreated,
                nameForLens = request.lensName
            )

            val createResult = lensRepository.createAsync(lens)

            if (!createResult.isSuccess) {
                val errorMsg = when (createResult) {
                    is Result.Failure -> createResult.errorMessage
                    else -> "Error creating lens"
                }
                return Result.failure(errorMsg)
            }

            val createdLens = createResult.data!!

            // Create compatibility relationships
            val compatibilities = request.compatibleCameraIds.map { cameraId ->
                LensCameraCompatibility(createdLens.id, cameraId)
            }

            val compatibilityResult = compatibilityRepository.createBatchAsync(compatibilities)

            if (!compatibilityResult.isSuccess) {
                // Lens was created but compatibility failed - log warning but don't fail
                // In a real implementation, you might want to log this
            }

            val lensDto = LensDto(
                id = createdLens.id,
                minMM = createdLens.minMM,
                maxMM = createdLens.maxMM,
                minFStop = createdLens.minFStop,
                maxFStop = createdLens.maxFStop,
                isPrime = createdLens.isPrime,
                isUserCreated = createdLens.isUserCreated,
                dateAdded = createdLens.dateAdded,
                displayName = createdLens.getDisplayName()
            )

            val resultDto = CreateLensResultDto(
                lens = lensDto,
                compatibleCameraIds = request.compatibleCameraIds,
                isSuccessful = true,
                errorMessage = ""
            )

            Result.success(resultDto)
        } catch (e: Exception) {
            val failureDto = CreateLensResultDto(
                isSuccessful = false,
                errorMessage = "Error creating lens: ${e.message}"
            )
            Result.success(failureDto)
        }
    }
}