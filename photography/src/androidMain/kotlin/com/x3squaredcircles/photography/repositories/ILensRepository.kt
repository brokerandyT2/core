// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/repositories/ILensRepository.kt
package com.x3squaredcircles.photography.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.Lens
import kotlinx.coroutines.Job

interface ILensRepository {
    
    /**
     * Creates a new lens
     */
    suspend fun createAsync(lens: Lens, cancellationToken: Job = Job()): Result<Lens>

    /**
     * Gets a lens by ID
     */
    suspend fun getByIdAsync(id: Int, cancellationToken: Job = Job()): Result<Lens>

    /**
     * Gets lenses with paging, user lenses first
     */
    suspend fun getPagedAsync(skip: Int, take: Int, cancellationToken: Job = Job()): Result<List<Lens>>

    /**
     * Gets all user-created lenses
     */
    suspend fun getUserLensesAsync(cancellationToken: Job = Job()): Result<List<Lens>>

    /**
     * Updates an existing lens
     */
    suspend fun updateAsync(lens: Lens, cancellationToken: Job = Job()): Result<Lens>

    /**
     * Deletes a lens
     */
    suspend fun deleteAsync(id: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Searches lenses by focal length range with fuzzy matching
     */
    suspend fun searchByFocalLengthAsync(focalLength: Double, cancellationToken: Job = Job()): Result<List<Lens>>

    /**
     * Gets lenses compatible with a specific camera
     */
    suspend fun getCompatibleLensesAsync(cameraBodyId: Int, cancellationToken: Job = Job()): Result<List<Lens>>

    /**
     * Gets total count of lenses
     */
    suspend fun getTotalCountAsync(cancellationToken: Job = Job()): Result<Int>
}