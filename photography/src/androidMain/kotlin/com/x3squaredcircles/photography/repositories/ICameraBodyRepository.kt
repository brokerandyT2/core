// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/repositories/ICameraBodyRepository.kt
package com.x3squaredcircles.photography.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photography.domain.enums.MountType
import kotlinx.coroutines.Job

interface ICameraBodyRepository {
    
    /**
     * Creates a new camera body
     */
    suspend fun createAsync(cameraBody: CameraBody, cancellationToken: Job = Job()): Result<CameraBody>

    /**
     * Gets a camera body by ID
     */
    suspend fun getByIdAsync(id: Int, cancellationToken: Job = Job()): Result<CameraBody>

    /**
     * Gets camera bodies with paging, user cameras first
     */
    suspend fun getPagedAsync(skip: Int, take: Int, cancellationToken: Job = Job()): Result<List<CameraBody>>

    /**
     * Gets all user-created camera bodies
     */
    suspend fun getUserCamerasAsync(cancellationToken: Job = Job()): Result<List<CameraBody>>

    /**
     * Updates an existing camera body
     */
    suspend fun updateAsync(cameraBody: CameraBody, cancellationToken: Job = Job()): Result<CameraBody>

    /**
     * Deletes a camera body
     */
    suspend fun deleteAsync(id: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Searches camera bodies by name with fuzzy matching
     */
    suspend fun searchByNameAsync(name: String, cancellationToken: Job = Job()): Result<List<CameraBody>>

    /**
     * Gets camera bodies by mount type
     */
    suspend fun getByMountTypeAsync(mountType: MountType, cancellationToken: Job = Job()): Result<List<CameraBody>>

    /**
     * Gets total count of camera bodies
     */
    suspend fun getTotalCountAsync(cancellationToken: Job = Job()): Result<Int>
}