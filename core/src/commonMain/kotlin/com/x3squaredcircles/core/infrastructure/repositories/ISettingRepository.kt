// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/ISettingRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Setting

interface ISettingRepository {
    suspend fun getByIdAsync(id: Int): Setting?
    suspend fun getByKeyAsync(key: String): Result<Setting>
    suspend fun getAllAsync(): Result<List<Setting>>
    suspend fun getByKeysAsync(keys: List<String>): List<Setting>
    suspend fun addAsync(setting: Setting): Setting
    suspend fun updateAsync(setting: Setting)
    suspend fun deleteAsync(setting: Setting)
    suspend fun upsertAsync(key: String, value: String, description: String? = null): Setting
    suspend fun getAllAsDictionaryAsync(): Result<Map<String, String>>
}
