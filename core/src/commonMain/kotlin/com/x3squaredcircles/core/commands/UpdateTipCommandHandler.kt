// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/UpdateTipCommandHandler.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.UpdateTipCommandResponse
import com.x3squaredcircles.core.infrastructure.repositories.ITipRepository
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator

class UpdateTipCommandHandler(
        private val tipRepository: ITipRepository,
        private val mediator: IMediator,
        private val unitOfWork: IUnitOfWork
) : ICommandHandler<UpdateTipCommand, Result<UpdateTipCommandResponse>> {
    override suspend fun handle(request: UpdateTipCommand): Result<UpdateTipCommandResponse> {
        return try {
            val tipResult = tipRepository.getByIdAsync(request.id)
            if (!tipResult.isSuccess || tipResult.data == null) {
                return Result.failure("Tip_Error_NotFound")
            }

            val tip = tipResult.data

            tip?.updateContent(request.title, request.content)

            tip?.updatePhotographySettings(request.fstop, request.shutterSpeed, request.iso)

            tip?.setLocalization(request.i8n)

            val updateResult = tipRepository.updateAsync(tip!!)
            if (!updateResult.isSuccess) {
                val errorMsg =
                        if (updateResult is Result.Failure) updateResult.errorMessage
                        else "Update failed"
                return Result.failure("Tip_Error_UpdateFailed: $errorMsg")
            }

            val updatedTip = updateResult.data!!

            val response =
                    UpdateTipCommandResponse(
                            id = updatedTip.id,
                            tipTypeId = updatedTip.tipTypeId,
                            title = updatedTip.title,
                            content = updatedTip.content,
                            fstop = updatedTip.fstop,
                            shutterSpeed = updatedTip.shutterSpeed,
                            iso = updatedTip.iso,
                            i8n = updatedTip.i8n
                    )

            Result.success(response)
        } catch (ex: Exception) {
            when (ex.message) {
                "Title cannot be empty" -> Result.failure("Tip_ValidationError_TitleRequired")
                "Content cannot be empty" -> Result.failure("Tip_ValidationError_ContentRequired")
                else -> Result.failure("Tip_Error_UpdateFailed: ${ex.message}")
            }
        }
    }
}
