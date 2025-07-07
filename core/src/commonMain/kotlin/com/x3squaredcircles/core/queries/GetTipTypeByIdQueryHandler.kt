// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetTipTypeByIdQueryHandler.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.infrastructure.repositories.ITipTypeRepository
import com.x3squaredcircles.core.mediator.IQueryHandler

class GetTipTypeByIdQueryHandler(private val tipTypeRepository: ITipTypeRepository) :
        IQueryHandler<GetTipTypeByIdQuery, Result<TipTypeDto>> {
    override suspend fun handle(request: GetTipTypeByIdQuery): Result<TipTypeDto> {
        return try {
            val tipType = tipTypeRepository.getByIdAsync(request.id)

            if (tipType == null) {
                Result.failure("TipType_Error_NotFoundById: ${request.id}")
            } else {
                val tipTypeDto = TipTypeDto(id = tipType.id, name = tipType.name, i8n = tipType.i8n)
                Result.success(tipTypeDto)
            }
        } catch (ex: Exception) {
            Result.failure("TipType_Error_RetrieveFailed: ${ex.message}")
        }
    }
}
// This query handler retrieves a TipType by its ID and returns it as a TipTypeDto.
// If the TipType is not found, it returns a failure result with an appropriate error message