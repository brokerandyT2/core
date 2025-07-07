// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/RemovePhotoCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.mediator.ICommand

data class RemovePhotoCommand(
    val locationId: Int
) : ICommand<Result<LocationDto>>