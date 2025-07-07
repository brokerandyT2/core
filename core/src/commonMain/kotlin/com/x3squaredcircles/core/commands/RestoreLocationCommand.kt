// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/RestoreLocationCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.mediator.ICommand

data class RestoreLocationCommand(
    val locationId: Int
) : ICommand<Result<LocationDto>>