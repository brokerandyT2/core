// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/DeleteLocationCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand

data class DeleteLocationCommand(
    val id: Int
) : ICommand<Result<Boolean>>