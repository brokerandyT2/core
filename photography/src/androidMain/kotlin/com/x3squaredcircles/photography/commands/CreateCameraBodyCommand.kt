// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/CreateCameraBodyCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photography.domain.enums.MountType

data class CreateCameraBodyCommand(
    val name: String = "",
    val sensorType: String = "",
    val sensorWidth: Double = 0.0,
    val sensorHeight: Double = 0.0,
    val mountType: MountType = MountType.Other,
    val isUserCreated: Boolean = true
) : ICommand<Result<CameraBodyDto>>