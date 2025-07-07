// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/CreateTipTypeCommand.kt
package com.x3squaredcircles.core.commands
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.mediator.ICommand

data class CreateTipTypeCommand(
val name: String = "",
val i8n: String = "en-US"
) : ICommand<Result<TipTypeDto>>