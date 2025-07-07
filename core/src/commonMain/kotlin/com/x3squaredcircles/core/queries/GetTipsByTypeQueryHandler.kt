// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetTipsByTypeQueryHandler.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipDto

import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.infrastructure.repositories.ITipRepository
class GetTipsByTypeQueryHandler(
private val tipRepository: ITipRepository,
private val mediator: IMediator
) : IQueryHandler<GetTipsByTypeQuery, Result<List<TipDto>>> {
override suspend fun handle(request: GetTipsByTypeQuery): Result<List<TipDto>> {
    return try {
        val result = tipRepository.getByTypeAsync(request.tipTypeId)

        when (result) {
            is Result.Success -> {
                val tipDtos = result.data!!.map { tip ->
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
            }
            is Result.Failure -> {
                Result.failure(result.errorMessage)
            }
            
        }
    } catch (ex: Exception) {
        Result.failure("Tip_Error_RetrieveFailed: ${ex.message}")
    }
}
}