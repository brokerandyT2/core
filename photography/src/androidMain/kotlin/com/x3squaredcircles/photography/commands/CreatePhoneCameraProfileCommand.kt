// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/CreatePhoneCameraProfileCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.PhoneCameraProfileDto

data class CreatePhoneCameraProfileCommand(
    val imagePath: String = "",
    val deleteImageAfterProcessing: Boolean = true
) : ICommand<Result<PhoneCameraProfileDto>>