// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/ITipTypeRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories
import com.x3squaredcircles.core.domain.entities.TipType
interface ITipTypeRepository {
suspend fun getByIdAsync(id: Int): TipType?
suspend fun getAllAsync(): List<TipType>
suspend fun addAsync(tipType: TipType): TipType
suspend fun updateAsync(tipType: TipType)
suspend fun deleteAsync(tipType: TipType)
suspend fun getByNameAsync(name: String): TipType?
suspend fun getWithTipsAsync(id: Int): TipType?
}