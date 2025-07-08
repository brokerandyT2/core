// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetAllSettingsQueryHandler.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Setting

import com.x3squaredcircles.core.mediator.IQueryHandler
class GetAllSettingsQueryHandler(
private val unitOfWork: IUnitOfWork
) : IQueryHandler<GetAllSettingsQuery, Result<List<GetAllSettingsQueryResponse>>> {
override suspend fun handle(request: GetAllSettingsQuery): Result<List<GetAllSettingsQueryResponse>> {
    return try {
        val result = unitOfWork.settings.getAllAsync()

        if (!result.isSuccess || result.data == null) {
            val errorMsg = if (result is Result.Failure<*>) result.errorMessage else "Settings retrieve failed"
            return Result.failure(errorMsg)
        }

        val settings = result.data

        val response = settings.map { setting: Setting ->
            GetAllSettingsQueryResponse(
                id = setting.id,
                key = setting.key,
                value = setting.value,
                description = setting.description,
                timestamp = setting.timestamp
            )
        }

        Result.success(response)
    } catch (ex: Exception) {
        Result.failure("Setting_Error_RetrieveFailed: ${ex.message}")
    }
}
}