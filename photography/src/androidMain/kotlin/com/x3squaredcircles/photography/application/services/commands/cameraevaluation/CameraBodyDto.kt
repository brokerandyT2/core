// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/cameraevaluation/CameraBodyDto.kt
package com.x3squaredcircles.photography.application.commands.cameraevaluation

import com.x3squaredcircles.photography.domain.enums.MountType
import kotlinx.datetime.LocalDateTime

data class CameraBodyDto(
    val id: Int = 0,
    val name: String = "",
    val sensorType: String = "",
    val sensorWidth: Double = 0.0,
    val sensorHeight: Double = 0.0,
    val mountType: MountType = MountType.Other,
    val isUserCreated: Boolean = false,
    val dateAdded: LocalDateTime,
    val displayName: String = ""
)