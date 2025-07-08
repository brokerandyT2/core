// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/DeleteLocationCommandHandler.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.core.mediator.IMediator

class DeleteLocationCommandHandler(
    private val locationRepository: ILocationRepository,
    private val mediator: IMediator,
    private val unitOfWork: IUnitOfWork
) : ICommandHandler<DeleteLocationCommand, Result<Boolean>> {

    override suspend fun handle(request: DeleteLocationCommand): Result<Boolean> {
        return try {
            val locationResult = locationRepository.getByIdAsync(request.id)

            if (!locationResult.isSuccess || locationResult.data == null) {
                return Result.failure("Location_Error_NotFound")
            }

            val location = locationResult.data!!
            // Soft delete - sets isDeleted = true
            location.delete()

            val updateResult = locationRepository.updateAsync(location)
            if (!updateResult.isSuccess) {
                return Result.failure("Location_Error_UpdateFailed")
            }

            Result.success(true)
        } catch (ex: Exception) {
            when {
                ex.message?.contains("LOCATION_IN_USE") == true -> 
                    Result.failure("Location_Error_CannotDeleteInUse")
                else -> 
                    Result.failure("Location_Error_DeleteFailed: ${ex.message}")
            }
        }
    }
}