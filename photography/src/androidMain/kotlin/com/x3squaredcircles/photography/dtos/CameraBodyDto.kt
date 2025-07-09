// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/CameraBodyDto.kt
package com.x3squaredcircles.photography.dtos

import com.x3squaredcircles.photography.domain.enums.MountType

data class CameraBodyDto(
    val id: Int = 0,
    val name: String = "",
    val sensorType: String = "",
    val sensorWidth: Double = 0.0,
    val sensorHeight: Double = 0.0,
    val mountType: MountType = MountType.Other,
    val isUserCreated: Boolean = false,
    val dateAdded: Long = 0L,
    val displayName: String = ""
) {
    fun getSensorDiagonal(): Double {
        return kotlin.math.sqrt(sensorWidth * sensorWidth + sensorHeight * sensorHeight)
    }

    fun getCropFactor(): Double {
        val fullFrameDiagonal = kotlin.math.sqrt(36.0 * 36.0 + 24.0 * 24.0)
        return fullFrameDiagonal / getSensorDiagonal()
    }

    fun isFullFrame(): Boolean {
        return getCropFactor() <= 1.1
    }

    fun getMountDisplayName(): String {
        return MountType.getDisplayName(mountType)
    }

    fun getMountBrandName(): String {
        return MountType.getBrandName(mountType)
    }

    fun getSensorDescription(): String {
        return "$sensorType (${String.format("%.1f", sensorWidth)}x${String.format("%.1f", sensorHeight)}mm)"
    }

    fun getFullDescription(): String {
        return "$displayName - ${getSensorDescription()} - ${getMountDisplayName()}"
    }
}