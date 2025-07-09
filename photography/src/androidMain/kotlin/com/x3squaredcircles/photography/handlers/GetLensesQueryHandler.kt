// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/handlers/GetLensesQueryHandler.kt
package com.x3squaredcircles.photography.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetLensesQuery
import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photography.dtos.LensDto
import com.x3squaredcircles.photography.dtos.GetLensesResultDto
import com.x3squaredcircles.photography.repositories.ILensRepository

class GetLensesQueryHandler(
    private val lensRepository: ILensRepository
) : IQueryHandler<GetLensesQuery, Result<GetLensesResultDto>> {

    override suspend fun handle(request: GetLensesQuery): Result<GetLensesResultDto> {
        return try {
            val lenses: List<Lens>
            val totalCount: Int

            when {
                request.compatibleWithCameraId != null -> {
                    val compatibleResult = lensRepository.getCompatibleLensesAsync(request.compatibleWithCameraId)
                    if (!compatibleResult.isSuccess) {
                        val errorMsg = when (compatibleResult) {
                            is Result.Failure -> compatibleResult.errorMessage
                            else -> "Error retrieving compatible lenses"
                        }
                        return Result.failure(errorMsg)
                    }

                    val allCompatible = compatibleResult.data ?: emptyList()
                    lenses = allCompatible.drop(request.skip).take(request.take)
                    totalCount = allCompatible.size
                }
                request.userLensesOnly -> {
                    val userLensesResult = lensRepository.getUserLensesAsync()
                    if (!userLensesResult.isSuccess) {
                        val errorMsg = when (userLensesResult) {
                            is Result.Failure -> userLensesResult.errorMessage
                            else -> "Error retrieving user lenses"
                        }
                        return Result.failure(errorMsg)
                    }

                    val allUserLenses = userLensesResult.data ?: emptyList()
                    lenses = allUserLenses.drop(request.skip).take(request.take)
                    totalCount = allUserLenses.size
                }
                else -> {
                    val pagedResult = lensRepository.getPagedAsync(request.skip, request.take)
                    if (!pagedResult.isSuccess) {
                        val errorMsg = when (pagedResult) {
                            is Result.Failure -> pagedResult.errorMessage
                            else -> "Error retrieving lenses"
                        }
                        return Result.failure(errorMsg)
                    }

                    val countResult = lensRepository.getTotalCountAsync()
                    if (!countResult.isSuccess) {
                        val errorMsg = when (countResult) {
                            is Result.Failure -> countResult.errorMessage
                            else -> "Error retrieving lens count"
                        }
                        return Result.failure(errorMsg)
                    }

                    lenses = pagedResult.data ?: emptyList()
                    totalCount = countResult.data ?: 0
                }
            }

            val lensDtos = lenses.map { lens ->
                LensDto(
                    id = lens.id,
                    minMM = lens.minMM,
                    maxMM = lens.maxMM,
                    minFStop = lens.minFStop,
                    maxFStop = lens.maxFStop,
                    isPrime = lens.isPrime,
                    isUserCreated = lens.isUserCreated,
                    dateAdded = lens.dateAdded,
                    displayName = lens.getDisplayName()
                )
            }

            val result = GetLensesResultDto(
                lenses = lensDtos,
                totalCount = totalCount,
                hasMore = (request.skip + request.take) < totalCount
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure("Error retrieving lenses: ${e.message}")
        }
    }
}