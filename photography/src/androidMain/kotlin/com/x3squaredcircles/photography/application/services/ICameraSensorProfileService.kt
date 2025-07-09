// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/ICameraSensorProfileService.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.application.commands.cameraevaluation.CameraBodyDto


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