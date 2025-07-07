// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/UpdateTipCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result

import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.core.dtos.UpdateTipCommandResponse

data class UpdateTipCommand(
    val id: Int,
    val tipTypeId: Int,
    val title: String = "",
    val content: String = "",
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val i8n: String = "en-US"
) : ICommand<Result<UpdateTipCommandResponse>>