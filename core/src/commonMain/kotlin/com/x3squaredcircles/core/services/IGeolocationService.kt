// core\src\commonMain\kotlin\com\x3squaredcircles\core\services\IGeolocationService.kt
package com.x3squaredcircles.core.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.GeolocationDto
import com.x3squaredcircles.core.enums.GeolocationAccuracy

import kotlinx.coroutines.Job

interface IGeolocationService {
    suspend fun getCurrentLocationAsync(cancellationToken: Job = Job()): Result<GeolocationDto>
    suspend fun isLocationEnabledAsync(cancellationToken: Job = Job()): Result<Boolean>
    suspend fun requestPermissionsAsync(cancellationToken: Job = Job()): Result<Boolean>
    suspend fun startTrackingAsync(accuracy: GeolocationAccuracy = GeolocationAccuracy.Medium, cancellationToken: Job = Job()): Result<Boolean>
    suspend fun stopTrackingAsync(cancellationToken: Job = Job()): Result<Boolean>
}