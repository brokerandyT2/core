// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/services/ICameraSensorProfileService.kt
package com.x3squaredcircles.photography.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import kotlinx.coroutines.Job

interface ICameraSensorProfileService {
    
    /**
     * Loads camera sensor profiles from JSON files in Resources/CameraSensorProfiles
     */
    suspend fun loadCameraSensorProfilesAsync(
        jsonContents: List<String>,
        cancellationToken: Job = Job()
    ): Result<List<CameraBodyDto>>
}