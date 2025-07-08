// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/CreateTipCommandHandler.kt
package com.x3squaredcircles.core.commands
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipDto
import com.x3squaredcircles.core.domain.entities.Tip

import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator
class CreateTipCommandHandler(
private val unitOfWork: IUnitOfWork,
private val mediator: IMediator
) : ICommandHandler<CreateTipCommand, Result<List<TipDto>>> {
override suspend fun handle(request: CreateTipCommand): Result<List<TipDto>> {
    return try {
        val tip = Tip(request.tipTypeId, request.title, request.content)

        if (request.fstop.isNotEmpty() || request.shutterSpeed.isNotEmpty() || request.iso.isNotEmpty()) {
            tip.updatePhotographySettings(
                request.fstop,
                request.shutterSpeed,
                request.iso
            )
        }

        if (request.i8n.isNotEmpty()) {
            tip.setLocalization(request.i8n)
        }

        val result = unitOfWork.tips.addAsync(tip)

        val createdTip = result

        val tipDto = TipDto(
            id = createdTip.id,
            tipTypeId = createdTip.tipTypeId,
            title = createdTip.title,
            content = createdTip.content,
            fstop = createdTip.fstop,
            shutterSpeed = createdTip.shutterSpeed,
            iso = createdTip.iso,
            i8n = createdTip.i8n
        )

        Result.success(listOf(tipDto))
    } catch (ex: Exception) {
        when (ex.message) {
            "Title cannot be empty" -> Result.failure("Tip_ValidationError_TitleRequired")
            "Invalid tip type" -> Result.failure("Tip_Error_InvalidTipType")
            else -> Result.failure("Tip_Error_CreateFailed: ${ex.message}")
        }
    }
}
}