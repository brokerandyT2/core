// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/DeleteSettingCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand

data class DeleteSettingCommand(
    val key: String = ""
) : ICommand<Result<Boolean>>