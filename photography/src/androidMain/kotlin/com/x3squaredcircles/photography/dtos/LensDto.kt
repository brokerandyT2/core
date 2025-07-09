// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/LensDto.kt
package com.x3squaredcircles.photography.dtos

data class LensDto(
    val id: Int = 0,
    val minMM: Double = 0.0,
    val maxMM: Double? = null,
    val minFStop: Double? = null,
    val maxFStop: Double? = null,
    val isPrime: Boolean = false,
    val isUserCreated: Boolean = false,
    val dateAdded: Long = 0L,
    val displayName: String = ""
) {
    fun getFocalLengthDescription(): String {
        return if (isPrime) {
            "${minMM.toInt()}mm"
        } else {
            "${minMM.toInt()}-${maxMM?.toInt()}mm"
        }
    }

    fun getApertureRange(): String {
        return when {
            minFStop == null -> "Unknown aperture"
            maxFStop == null || kotlin.math.abs(maxFStop - minFStop) < 0.1 -> "f/${String.format("%.1f", minFStop)}"
            else -> "f/${String.format("%.1f", minFStop)}-${String.format("%.1f", maxFStop)}"
        }
    }

    fun isWideAngle(): Boolean {
        return minMM < 35.0
    }

    fun isStandard(): Boolean {
        return minMM >= 35.0 && minMM <= 85.0
    }

    fun isTelephoto(): Boolean {
        return minMM > 85.0
    }

    fun covers35mmEquivalent(focalLength: Double): Boolean {
        return if (isPrime) {
            kotlin.math.abs(minMM - focalLength) <= 5.0
        } else {
            maxMM?.let { focalLength >= minMM && focalLength <= it } ?: false
        }
    }

    fun getFullDescription(): String {
        return "$displayName - ${getFocalLengthDescription()} ${getApertureRange()}"
    }
}