// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetAllTipTypesQueryHandler.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipTypeDto

import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.infrastructure.repositories.ITipRepository
import com.x3squaredcircles.core.infrastructure.repositories.ITipTypeRepository
import com.x3squaredcircles.core.domain.entities.TipType

class GetAllTipTypesQueryHandler(
private val tipTypeRepository: ITipTypeRepository,
private val mediator: IMediator
) : IQueryHandler<GetAllTipTypesQuery, Result<List<TipTypeDto>>> {
override suspend fun handle(request: GetAllTipTypesQuery): Result<List<TipTypeDto>> {
    return try {
       
        val result = tipTypeRepository.getAllAsync()
        
        
        if (result.size == 0) {
            Result.failure("TipType_Error_ListRetrieveFailed")
        } else {
            val tipTypeDtos = result.map { tipType: TipType ->
                TipTypeDto(
                    id = tipType.id,
                    name = tipType.name,
                    i8n = tipType.i8n
                )
            }
            
            Result.success(tipTypeDtos)
        }
    } catch (ex: Exception) {
        Result.failure("TipType_Error_ListRetrieveFailedWithException: ${ex.message}")
    }
}
}