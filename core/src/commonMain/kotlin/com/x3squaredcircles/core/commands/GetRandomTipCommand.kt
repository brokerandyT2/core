// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/GetRandomTipCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipDto
import com.x3squaredcircles.core.mediator.ICommand

data class GetRandomTipCommand(
    val tipTypeId: Int
) : ICommand<Result<TipDto>>