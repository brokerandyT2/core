// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/AttachPhotoCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.mediator.ICommand

data class AttachPhotoCommand(
    val locationId: Int,
    val photoPath: String = ""
) : ICommand<Result<LocationDto>>