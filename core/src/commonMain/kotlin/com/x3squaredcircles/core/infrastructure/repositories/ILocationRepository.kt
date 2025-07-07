// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/ILocationRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Location

interface ILocationRepository {
    suspend fun getByIdAsync(id: Int): Result<Location>
    suspend fun getAllAsync(): Result<List<Location>>
    suspend fun getActiveAsync(): Result<List<Location>>
    suspend fun createAsync(location: Location): Result<Location>
    suspend fun updateAsync(location: Location): Result<Location>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun getByTitleAsync(title: String): Result<Location>
    suspend fun getNearbyAsync(
            latitude: Double,
            longitude: Double,
            distanceKm: Double
    ): Result<List<Location>>
    suspend fun getPagedAsync(
            pageNumber: Int,
            pageSize: Int,
            searchTerm: String? = null,
            includeDeleted: Boolean = false
    ): Result<PagedList<Location>>
    suspend fun existsByIdAsync(id: Int): Result<Boolean>
}

data class PagedList<T>(
        val items: List<T>,
        val pageNumber: Int,
        val pageSize: Int,
        val totalCount: Int
) {
    val totalPages: Int = (totalCount + pageSize - 1) / pageSize
    val hasPreviousPage: Boolean = pageNumber > 1
    val hasNextPage: Boolean = pageNumber < totalPages
}
