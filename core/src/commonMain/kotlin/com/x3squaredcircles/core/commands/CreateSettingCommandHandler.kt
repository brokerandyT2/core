// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/CreateSettingCommandHandler.kt
package com.x3squaredcircles.core.commands
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.SettingDto
import com.x3squaredcircles.core.domain.entities.Setting

import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator
data class CreateSettingCommand(
val key: String = "",
val value: String = "",
val description: String = ""
) : com.x3squaredcircles.core.mediator.ICommand<Result<CreateSettingCommandResponse>>
data class CreateSettingCommandResponse(
val id: Int,
val key: String,
val value: String,
val description: String,
val timestamp: Long
)
class CreateSettingCommandHandler(
private val unitOfWork: IUnitOfWork,
private val mediator: IMediator
) : ICommandHandler<CreateSettingCommand, Result<CreateSettingCommandResponse>> {
override suspend fun handle(request: CreateSettingCommand): Result<CreateSettingCommandResponse> {
    return try {
        val existingSettingResult = unitOfWork.settings.getByKeyAsync(request.key)

        if (existingSettingResult.isSuccess && existingSettingResult.data != null) {
            return Result.failure("Setting_Error_KeyAlreadyExists: ${request.key}")
        }

        val setting = Setting(request.key, request.value, request.description)

        val result = unitOfWork.settings.addAsync(setting)

        val createdSetting = result

        val response = CreateSettingCommandResponse(
            id = createdSetting.id,
            key = createdSetting.key,
            value = createdSetting.value,
            description = createdSetting.description,
            timestamp = createdSetting.timestamp
        )

        Result.success(response)
    } catch (ex: Exception) {
        when (ex.message) {
            "Key cannot be empty" -> Result.failure("Setting_ValidationError_KeyRequired")
            "Value cannot be null" -> Result.failure("Setting_ValidationError_ValueRequired")
            else -> Result.failure("Setting_Error_CreateFailed: ${ex.message}")
        }
    }
}
}