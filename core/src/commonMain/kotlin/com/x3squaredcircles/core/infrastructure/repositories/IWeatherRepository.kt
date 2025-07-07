// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/IWeatherRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Weather

interface IWeatherRepository {
    suspend fun getByIdAsync(id: Int): Weather?
    suspend fun getByLocationIdAsync(locationId: Int): Weather?
    suspend fun addAsync(weather: Weather): Weather
    suspend fun updateAsync(weather: Weather)
    suspend fun deleteAsync(weather: Weather)
    suspend fun getRecentAsync(count: Int = 10): List<Weather>
    suspend fun getExpiredAsync(maxAge: Long): List<Weather>
}
