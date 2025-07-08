// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetAllTipsQueryHandler.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipDto
import com.x3squaredcircles.core.domain.entities.Tip

import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.core.queries.GetAllTipsQuery
class GetAllTipsQueryHandler(
private val unitOfWork: IUnitOfWork
) : IQueryHandler<GetAllTipsQuery, Result<List<TipDto>>> {
override suspend fun handle(request: GetAllTipsQuery): Result<List<TipDto>> {
    return try {
        val result = unitOfWork.tips.getAllAsync()

        if (!result.isSuccess) {
            val errorMsg = if (result is Result.Failure<List<Tip>>) result.errorMessage else "Tip_Error_RetrieveFailed"
            return Result.failure(errorMsg)
        }

        val tips = result.data ?: emptyList<Tip>()
        val tipDtos = tips.map { tip: Tip ->
            TipDto(
                id = tip.id,
                tipTypeId = tip.tipTypeId,
                title = tip.title,
                content = tip.content,
                fstop = tip.fstop,
                shutterSpeed = tip.shutterSpeed,
                iso = tip.iso,
                i8n = tip.i8n
            )
        }

        Result.success(tipDtos)
    } catch (ex: Exception) {
        Result.failure("Tip_Error_RetrieveFailed: ${ex.message}")
    }
}
}