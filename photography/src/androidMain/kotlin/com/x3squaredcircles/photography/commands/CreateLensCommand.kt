// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/CreateLensCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.CreateLensResultDto

data class CreateLensCommand(
    val minMM: Double = 0.0,
    val maxMM: Double? = null,
    val minFStop: Double? = null,
    val maxFStop: Double? = null,
    val isUserCreated: Boolean = true,
    val lensName: String = "",
    val compatibleCameraIds: List<Int> = emptyList()
) : ICommand<Result<CreateLensResultDto>>