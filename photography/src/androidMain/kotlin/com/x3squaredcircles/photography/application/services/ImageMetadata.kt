// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/ImageMetadata.kt
package com.x3squaredcircles.photography.application.services

/**
 * Image metadata information extracted from EXIF data
 */
data class ImageMetadata(
    val width: Int,
    val height: Int,
    val aperture: String?,
    val shutterSpeed: String?,
    val iso: Int?,
    val focalLength: String?,
    val camera: String?,
    val lens: String?,
    val timestamp: Long?
)