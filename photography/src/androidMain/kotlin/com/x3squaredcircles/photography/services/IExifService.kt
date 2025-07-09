// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/services/IExifService.kt
package com.x3squaredcircles.photography.services

import com.x3squaredcircles.core.Result
import kotlinx.coroutines.Job

interface IExifService {
    
    /**
     * Extracts EXIF data from an image file
     */
    suspend fun extractExifDataAsync(imagePath: String, cancellationToken: Job = Job()): Result<ExifData>

    /**
     * Checks if the image contains the required EXIF data for camera calibration
     */
    suspend fun hasRequiredExifDataAsync(imagePath: String, cancellationToken: Job = Job()): Result<Boolean>
}

data class ExifData(
    val focalLength: Double? = null,
    val cameraModel: String = "",
    val cameraMake: String = "",
    val dateTaken: Long? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val aperture: Double? = null,
    val lensModel: String = ""
) {
    val hasValidFocalLength: Boolean
        get() = focalLength != null && focalLength > 0
    
    val fullCameraModel: String
        get() = "$cameraMake $cameraModel".trim()
}