// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/handlers/CreatePhoneCameraProfileCommandHandler.kt
package com.x3squaredcircles.photography.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.CreatePhoneCameraProfileCommand
import com.x3squaredcircles.photography.domain.entities.PhoneCameraProfile
import com.x3squaredcircles.photography.dtos.PhoneCameraProfileDto
import com.x3squaredcircles.photography.repositories.IPhoneCameraProfileRepository


class CreatePhoneCameraProfileCommandHandler(
    private val exifService: IExifService,
    private val fovCalculationService: IFOVCalculationService,
    private val repository: IPhoneCameraProfileRepository
) : ICommandHandler<CreatePhoneCameraProfileCommand, Result<PhoneCameraProfileDto>> {

    override suspend fun handle(request: CreatePhoneCameraProfileCommand): Result<PhoneCameraProfileDto> {
        return try {
            if (request.imagePath.isBlank()) {
                return createFailureDto("Image path is required")
            }

            // Step 1: Extract EXIF data
            val exifResult = exifService.extractExifDataAsync(request.imagePath)
            if (!exifResult.isSuccess) {
                return createFailureDto("Failed to extract EXIF data: ${getErrorMessage(exifResult)}")
            }

            val exifData = exifResult.data!!

            // Step 2: Validate required EXIF data
            if (!exifData.hasValidFocalLength) {
                return createFailureDto("Image does not contain valid focal length data")
            }

            if (exifData.fullCameraModel.isBlank()) {
                return createFailureDto("Missing camera model information")
            }

            // Step 3: Create phone camera profile
            val profileResult = fovCalculationService.createPhoneCameraProfileAsync(
                exifData.fullCameraModel,
                exifData.focalLength
            )

            if (!profileResult.isSuccess) {
                return createFailureDto("Failed to create profile: ${getErrorMessage(profileResult)}")
            }

            val profile = profileResult.data!!

            // Step 4: Deactivate existing profiles
            deactivateExistingProfiles()

            // Step 5: Save the profile
            val saveResult = repository.createAsync(profile)

            if (!saveResult.isSuccess) {
                return createFailureDto("Failed to save profile: ${getErrorMessage(saveResult)}")
            }

            val savedProfile = saveResult.data!!

            val dto = PhoneCameraProfileDto(
                id = savedProfile.id,
                phoneModel = savedProfile.phoneModel,
                mainLensFocalLength = savedProfile.mainLensFocalLength,
                mainLensFOV = savedProfile.mainLensFOV,
                ultraWideFocalLength = savedProfile.ultraWideFocalLength,
                telephotoFocalLength = savedProfile.telephotoFocalLength,
                dateCalibrated = savedProfile.dateCalibrated,
                isActive = savedProfile.isActive,
                isCalibrationSuccessful = true,
                errorMessage = ""
            )

            Result.success(dto)
        } catch (e: Exception) {
            createFailureDto("Error creating phone camera profile: ${e.message}")
        }
    }

    private suspend fun deactivateExistingProfiles() {
        try {
            val existingProfilesResult = repository.getAllAsync()
            if (existingProfilesResult.isSuccess && existingProfilesResult.data != null) {
                for (existingProfile in existingProfilesResult.data!!) {
                    if (existingProfile.isActive) {
                        existingProfile.deactivate()
                        repository.updateAsync(existingProfile)
                    }
                }
            }
        } catch (e: Exception) {
            // Log warning but don't fail the operation
        }
    }

    private fun createFailureDto(errorMessage: String): Result<PhoneCameraProfileDto> {
        val dto = PhoneCameraProfileDto(
            isCalibrationSuccessful = false,
            errorMessage = errorMessage
        )
        return Result.success(dto)
    }

    private fun getErrorMessage(result: Result<*>): String {
        return when (result) {
            is Result.Failure -> result.errorMessage
            else -> "Unknown error"
        }
    }
}