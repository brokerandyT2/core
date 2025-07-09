// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/repositories/ILensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photography.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility
import kotlinx.coroutines.Job

interface ILensCameraCompatibilityRepository {
    
    /**
     * Creates a new lens-camera compatibility relationship
     */
    suspend fun createAsync(compatibility: LensCameraCompatibility, cancellationToken: Job = Job()): Result<LensCameraCompatibility>

    /**
     * Creates multiple lens-camera compatibility relationships
     */
    suspend fun createBatchAsync(compatibilities: List<LensCameraCompatibility>, cancellationToken: Job = Job()): Result<List<LensCameraCompatibility>>

    /**
     * Gets all compatibility relationships for a lens
     */
    suspend fun getByLensIdAsync(lensId: Int, cancellationToken: Job = Job()): Result<List<LensCameraCompatibility>>

    /**
     * Gets all compatibility relationships for a camera
     */
    suspend fun getByCameraIdAsync(cameraBodyId: Int, cancellationToken: Job = Job()): Result<List<LensCameraCompatibility>>

    /**
     * Checks if a lens-camera compatibility exists
     */
    suspend fun existsAsync(lensId: Int, cameraBodyId: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Deletes a specific lens-camera compatibility
     */
    suspend fun deleteAsync(lensId: Int, cameraBodyId: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Deletes all compatibility relationships for a lens
     */
    suspend fun deleteByLensIdAsync(lensId: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Deletes all compatibility relationships for a camera
     */
    suspend fun deleteByCameraIdAsync(cameraBodyId: Int, cancellationToken: Job = Job()): Result<Boolean>
}