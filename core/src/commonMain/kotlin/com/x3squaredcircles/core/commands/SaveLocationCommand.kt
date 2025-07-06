// core\src\commonMain\kotlin\com\x3squaredcircles\core\commands\SaveLocationCommand.kt
package com.x3squaredcircles.core.commands
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.LocationDto
import com.x3squaredcircles.core.mediator.ICommand
data class SaveLocationCommand(
val id: Int? = null,
val title: String = "",
val description: String = "",
val latitude: Double = 0.0,
val longitude: Double = 0.0,
val city: String = "",
val state: String = "",
val photoPath: String? = null
) : ICommand<Result<LocationDto>>