// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/LensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.database.IDatabaseContext
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility
import com.x3squaredcircles.photography.repositories.ILensCameraCompatibilityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
interface ILogger {
fun logInformation(message: String)
fun logError(message: String, exception: Exception? = null)
fun logWarning(message: String)
}
class LensCameraCompatibilityRepository(
private val context: IDatabaseContext,
private val logger: ILogger
) : ILensCameraCompatibilityRepository {
companion object {
    private const val COMPATIBILITY_ERROR_CANNOT_BE_NULL = "Compatibility cannot be null"
    private const val COMPATIBILITY_ERROR_CREATING = "Error creating compatibility: %s"
    private const val COMPATIBILITY_ERROR_CREATING_BATCH = "Error creating compatibilities: %s"
    private const val COMPATIBILITY_ERROR_GETTING = "Error getting compatibilities: %s"
    private const val COMPATIBILITY_ERROR_CHECKING = "Error checking compatibility: %s"
    private const val COMPATIBILITY_ERROR_DELETING = "Error deleting compatibility: %s"
    private const val COMPATIBILITY_ERROR_DELETING_BATCH = "Error deleting compatibilities: %s"
}

override suspend fun createAsync(compatibility: LensCameraCompatibility, cancellationToken: Job): Result<LensCameraCompatibility> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.insertAsync(compatibility)
        }

        logger.logInformation("Created lens-camera compatibility: LensId=${compatibility.lensId}, CameraId=${compatibility.cameraBodyId}")
        Result.success(compatibility)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error creating lens-camera compatibility", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_CREATING, e.message))
    }
}

override suspend fun createBatchAsync(compatibilities: List<LensCameraCompatibility>, cancellationToken: Job): Result<List<LensCameraCompatibility>> {
    return try {
        cancellationToken.ensureActive()

        if (compatibilities.isEmpty()) {
            return Result.success(emptyList())
        }

        withContext(Dispatchers.IO) {
            context.insertBatchAsync(compatibilities)
        }

        logger.logInformation("Created ${compatibilities.size} lens-camera compatibilities")
        Result.success(compatibilities)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error creating batch lens-camera compatibilities", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_CREATING_BATCH, e.message))
    }
}

override suspend fun getByLensIdAsync(lensId: Int, cancellationToken: Job): Result<List<LensCameraCompatibility>> {
    return try {
        cancellationToken.ensureActive()

        val compatibilities = withContext(Dispatchers.IO) {
            context.table<LensCameraCompatibility>()
                .where { it.lensId == lensId }
               as List<LensCameraCompatibility>()
        }

        Result.success(compatibilities)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting compatibilities by lens ID: $lensId", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_GETTING, e.message))
    }
}

override suspend fun getByCameraIdAsync(cameraBodyId: Int, cancellationToken: Job): Result<List<LensCameraCompatibility>> {
    return try {
        cancellationToken.ensureActive()

        val compatibilities = withContext(Dispatchers.IO) {
            context.table<LensCameraCompatibility>()
                .where { it.cameraBodyId == cameraBodyId }
                as List<LensCameraCompatibility>()
        }

        Result.success(compatibilities)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting compatibilities by camera ID: $cameraBodyId", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_GETTING, e.message))
    }
}

override suspend fun existsAsync(lensId: Int, cameraBodyId: Int, cancellationToken: Job): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        val exists = withContext(Dispatchers.IO) {
            val count = context.table<LensCameraCompatibility>()
                .where { it.lensId == lensId && it.cameraBodyId == cameraBodyId }
                as List<LensCameraCompatibility>().size >0
            
        } as Boolean

        return Result.success(exists)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error checking compatibility existence: LensId=$lensId, CameraId=$cameraBodyId", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_CHECKING, e.message))
    }
}

override suspend fun deleteAsync(lensId: Int, cameraBodyId: Int, cancellationToken: Job): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        val result = withContext(Dispatchers.IO) {
            val compatibility = context.table<LensCameraCompatibility>()
                .where { it.lensId == lensId && it.cameraBodyId == cameraBodyId }
               
            if (compatibility != null) {
                context.deleteAsync(compatibility)
            } else {
                0
            }
        }

        Result.success(result > 0)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error deleting compatibility: LensId=$lensId, CameraId=$cameraBodyId", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_DELETING, e.message))
    }
}

override suspend fun deleteByLensIdAsync(lensId: Int, cancellationToken: Job): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        val result = withContext(Dispatchers.IO) {
            val compatibilities = context.table<LensCameraCompatibility>()
                .where { it.lensId == lensId }
                as List<LensCameraCompatibility>()

            var deletedCount = 0
            compatibilities.forEach { compatibility ->
                deletedCount += context.deleteAsync(compatibility)
            }
            deletedCount
        }

        Result.success(result > 0)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error deleting compatibilities by lens ID: $lensId", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_DELETING_BATCH, e.message))
    }
}

override suspend fun deleteByCameraIdAsync(cameraBodyId: Int, cancellationToken: Job): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        val result = withContext(Dispatchers.IO) {
            val compatibilities = context.table<LensCameraCompatibility>()
                .where { it.cameraBodyId == cameraBodyId }
                as List<LensCameraCompatibility>()
            var deletedCount = 0
            compatibilities.forEach { compatibility ->
                deletedCount += context.deleteAsync(compatibility)
            }
            deletedCount
        }

        Result.success(result > 0)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error deleting compatibilities by camera ID: $cameraBodyId", e)
        Result.failure(String.format(COMPATIBILITY_ERROR_DELETING_BATCH, e.message))
    }
}
}