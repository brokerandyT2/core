// photography/src/androidMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ExifService.kt
package com.x3squaredcircles.photography.infrastructure.services

import android.media.ExifInterface
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.services.ExifData
import com.x3squaredcircles.photography.services.IExifService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExifService(
    private val logger: ILoggingService
) : IExifService {

    companion object {
        private const val MAX_RETRIES = 5
        private const val DELAY_MS = 200L
    }

    override suspend fun extractExifDataAsync(imagePath: String, cancellationToken: Job): Result<ExifData> {
        return try {
            if (cancellationToken.isCancelled) {
                return Result.failure("Operation was cancelled")
            }

            if (imagePath.isBlank()) {
                return Result.failure("Image path cannot be null or empty")
            }

            if (!File(imagePath).exists()) {
                return Result.failure("Image file does not exist")
            }

            withContext(Dispatchers.IO) {
                for (attempt in 0 until MAX_RETRIES) {
                    try {
                        if (cancellationToken.isCancelled) {
                            return@withContext Result.failure<ExifData>("Operation was cancelled")
                        }

                        val exifInterface = ExifInterface(imagePath)
                        val exifData = extractDataFromExifInterface(exifInterface)
                        return@withContext Result.success(exifData)
                    } catch (ioEx: IOException) {
                        if (attempt < MAX_RETRIES - 1) {
                            logger.logWarning("File locked on attempt ${attempt + 1}, retrying in ${DELAY_MS * (attempt + 1)}ms: $imagePath", ioEx)
                            delay(DELAY_MS * (attempt + 1))
                            continue
                        } else {
                            logger.logError("Failed to extract EXIF data from $imagePath on attempt ${attempt + 1}", ioEx)
                            return@withContext Result.failure("Failed to extract EXIF data after $MAX_RETRIES attempts: ${ioEx.message}")
                        }
                    } catch (ex: Exception) {
                        logger.logError("Failed to extract EXIF data from $imagePath on attempt ${attempt + 1}", ex)
                        if (attempt == MAX_RETRIES - 1) {
                            return@withContext Result.failure("Failed to extract EXIF data after $MAX_RETRIES attempts: ${ex.message}")
                        }
                    }
                }
                Result.failure("Failed to extract EXIF data after $MAX_RETRIES attempts")
            }
        } catch (ex: Exception) {
            logger.logError("Error extracting EXIF data from $imagePath", ex)
            Result.failure("Error extracting EXIF data: ${ex.message}")
        }
    }

    override suspend fun hasRequiredExifDataAsync(imagePath: String, cancellationToken: Job): Result<Boolean> {
        return try {
            val exifResult = extractExifDataAsync(imagePath, cancellationToken)

            if (!exifResult.isSuccess) {
                return Result.success(false)
            }

            val exifData = exifResult.data!!
            val hasRequired = exifData.hasValidFocalLength && exifData.fullCameraModel.isNotBlank()

            Result.success(hasRequired)
        } catch (ex: Exception) {
            logger.logError("Error checking EXIF data requirements for $imagePath", ex)
            Result.failure("Error checking EXIF data: ${ex.message}")
        }
    }

    private fun extractDataFromExifInterface(exifInterface: ExifInterface): ExifData {
        return ExifData(
            focalLength = exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0).takeIf { it > 0.0 },
            cameraModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL)?.trim() ?: "",
            cameraMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE)?.trim() ?: "",
            dateTaken = parseDateTime(exifInterface.getAttribute(ExifInterface.TAG_DATETIME)),
            imageWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0).takeIf { it > 0 },
            imageHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0).takeIf { it > 0 },
            aperture = exifInterface.getAttributeDouble(ExifInterface.TAG_F_NUMBER, 0.0).takeIf { it > 0.0 },
            lensModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL)?.trim() ?: ""
        )
    }

    private fun parseDateTime(dateTimeString: String?): Long? {
        if (dateTimeString.isNullOrBlank()) return null

        return try {
            val format = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)
            format.parse(dateTimeString)?.time
        } catch (ex: Exception) {
            logger.logWarning("Failed to parse EXIF date time: $dateTimeString", ex)
            null
        }
    }
}