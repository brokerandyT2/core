// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/LensCameraCompatibilityDto.kt
package com.x3squaredcircles.photography.dtos

data class LensCameraCompatibilityDto(
    val id: Int = 0,
    val lensId: Int = 0,
    val cameraBodyId: Int = 0,
    val dateAdded: Long = 0L
) {
    fun getCompatibilityKey(): String {
        return "${lensId}_${cameraBodyId}"
    }

    fun isValidCompatibility(): Boolean {
        return lensId > 0 && cameraBodyId > 0
    }
}