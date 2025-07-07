// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/ITipRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Tip

interface ITipRepository {
    suspend fun getByIdAsync(id: Int): Result<Tip>
    suspend fun getAllAsync(): Result<List<Tip>>
    suspend fun getByTypeAsync(tipTypeId: Int): Result<List<Tip>>
    suspend fun createAsync(tip: Tip): Result<Tip>
    suspend fun updateAsync(tip: Tip): Result<Tip>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun getRandomByTypeAsync(tipTypeId: Int): Result<Tip>
    suspend fun getByTitleAsync(title: String): Result<Tip>
}
