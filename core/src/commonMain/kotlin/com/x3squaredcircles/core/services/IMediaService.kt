// core\src\commonMain\kotlin\com\x3squaredcircles\core\services\IMediaService.kt
package com.x3squaredcircles.core.services

import com.x3squaredcircles.core.Result
import kotlinx.coroutines.Job

interface IMediaService {
    suspend fun capturePhotoAsync(cancellationToken: Job = Job()): Result<String>
    suspend fun pickPhotoAsync(cancellationToken: Job = Job()): Result<String>
    suspend fun isCaptureSupported(cancellationToken: Job = Job()): Result<Boolean>
    suspend fun deletePhotoAsync(filePath: String, cancellationToken: Job = Job()): Result<Boolean>
    fun getPhotoStorageDirectory(): String
}