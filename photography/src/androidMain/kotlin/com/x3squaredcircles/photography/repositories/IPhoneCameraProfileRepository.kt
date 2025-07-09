// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/repositories/IPhoneCameraProfileRepository.kt
package com.x3squaredcircles.photography.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.PhoneCameraProfile
import kotlinx.coroutines.Job

interface IPhoneCameraProfileRepository {
    
    /**
     * Creates a new phone camera profile
     */
    suspend fun createAsync(profile: PhoneCameraProfile, cancellationToken: Job = Job()): Result<PhoneCameraProfile>

    /**
     * Gets the active phone camera profile
     */
    suspend fun getActiveProfileAsync(cancellationToken: Job = Job()): Result<PhoneCameraProfile>

    /**
     * Gets a phone camera profile by ID
     */
    suspend fun getByIdAsync(id: Int, cancellationToken: Job = Job()): Result<PhoneCameraProfile>

    /**
     * Gets all phone camera profiles
     */
    suspend fun getAllAsync(cancellationToken: Job = Job()): Result<List<PhoneCameraProfile>>

    /**
     * Updates an existing phone camera profile
     */
    suspend fun updateAsync(profile: PhoneCameraProfile, cancellationToken: Job = Job()): Result<PhoneCameraProfile>

    /**
     * Deletes a phone camera profile
     */
    suspend fun deleteAsync(id: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Sets a profile as the active one (deactivates others)
     */
    suspend fun setActiveProfileAsync(profileId: Int, cancellationToken: Job = Job()): Result<Boolean>

    /**
     * Gets profiles by phone model
     */
    suspend fun getByPhoneModelAsync(phoneModel: String, cancellationToken: Job = Job()): Result<List<PhoneCameraProfile>>
}