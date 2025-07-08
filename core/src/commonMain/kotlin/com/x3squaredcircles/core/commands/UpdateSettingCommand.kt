// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/UpdateSettingCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand

data class UpdateSettingCommand(
    val key: String = "",
    val value: String = "",
    val description: String? = null
) : ICommand<Result<UpdateSettingCommandResponse>>

data class UpdateSettingCommandResponse(
    val id: Int,
    val key: String,
    val value: String,
    val description: String,
    val timestamp: Long
)