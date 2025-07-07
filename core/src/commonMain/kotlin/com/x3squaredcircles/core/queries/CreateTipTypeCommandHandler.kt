// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/CreateTipTypeCommandHandler.kt
package com.x3squaredcircles.core.commands
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.domain.entities.TipType
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.infrastructure.repositories.ITipTypeRepository

class CreateTipTypeCommandHandler(
private val tipTypeRepository: ITipTypeRepository,
private val mediator: IMediator
) : ICommandHandler<CreateTipTypeCommand, Result<TipTypeDto>> {
override suspend fun handle(request: CreateTipTypeCommand): Result<TipTypeDto> {
    return try {
        val tipType = TipType(request.name)
        tipType.setLocalization(request.i8n)

        val result = tipTypeRepository.addAsync(tipType)

        val createdTipType = result
        val tipTypeDto = TipTypeDto(
            id = createdTipType.id,
            name = createdTipType.name,
            i8n = createdTipType.i8n
        )

        Result.success(tipTypeDto)
    } catch (ex: Exception) {
        when (ex.message) {
            "Name cannot be empty" -> Result.failure("TipType_Error_NameInvalid")
            else -> Result.failure("TipType_Error_CreateFailed: ${ex.message}")
        }
    }
}
}