// core\src\commonMain\kotlin\com\x3squaredcircles\core\commands\UpdateWeatherCommand.kt
package com.x3squaredcircles.core.commands
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.WeatherDataDto
import com.x3squaredcircles.core.mediator.ICommand

data class UpdateWeatherCommand(
val locationId: Int,
val forceUpdate: Boolean = false
) : ICommand<Result<WeatherDataDto>>