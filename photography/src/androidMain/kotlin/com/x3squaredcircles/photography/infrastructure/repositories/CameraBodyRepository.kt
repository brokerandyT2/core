// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/CameraBodyRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.core.infrastructure.database.IDatabaseContext
import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photography.domain.enums.MountType
import com.x3squaredcircles.photography.repositories.ICameraBodyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
class CameraBodyRepository(
private val context: IDatabaseContext,
private val logger: ILoggingService
) : ICameraBodyRepository {
companion object {
    private const val CAMERA_BODY_ERROR_CANNOT_BE_NULL = "Camera body cannot be null"
    private const val CAMERA_BODY_ERROR_NOT_FOUND = "Camera body not found"
    private const val CAMERA_BODY_ERROR_CREATING = "Error creating camera body: %s"
    private const val CAMERA_BODY_ERROR_GETTING = "Error getting camera body: %s"
    private const val CAMERA_BODY_ERROR_GETTING_CAMERAS = "Error getting camera bodies: %s"
    private const val CAMERA_BODY_ERROR_GETTING_USER_CAMERAS = "Error getting user cameras: %s"
    private const val CAMERA_BODY_ERROR_UPDATING = "Error updating camera body: %s"
    private const val CAMERA_BODY_ERROR_DELETING = "Error deleting camera body: %s"
    private const val CAMERA_BODY_ERROR_SEARCHING = "Error searching camera bodies: %s"
    private const val CAMERA_BODY_ERROR_GETTING_COUNT = "Error getting camera body count: %s"
}

override suspend fun createAsync(cameraBody: CameraBody, cancellationToken: Job): Result<CameraBody> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.insertAsync(cameraBody)
        }

        logger.logInfo("Created camera body with ID: ${cameraBody.id}")
        Result.success(cameraBody)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error creating camera body: ${cameraBody.name}", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_CREATING, e.message))
    }
}

override suspend fun getByIdAsync(id: Int, cancellationToken: Job): Result<CameraBody> {
    return try {
        cancellationToken.ensureActive()

        val cameraBody = withContext(Dispatchers.IO) {
            val cameras = context.table<CameraBody>()
                .where { it.id == id } as List<CameraBody>
            
            cameras.firstOrNull()
        }

        if (cameraBody != null) {
            Result.success(cameraBody)
        } else {
            Result.failure(CAMERA_BODY_ERROR_NOT_FOUND)
        }
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting camera body by ID: $id", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_GETTING, e.message))
    }
}

override suspend fun getPagedAsync(skip: Int, take: Int, cancellationToken: Job): Result<List<CameraBody>> {
    return try {
        cancellationToken.ensureActive()

        val cameraBodies = withContext(Dispatchers.IO) {
            val allCameras = context.table<CameraBody>() as List<CameraBody>()
            
            allCameras
                .sortedWith(compareBy({ if (it.isUserCreated) 0 else 1 }, { it.name }))
                .drop(skip)
                .take(take)
        }

        Result.success(cameraBodies)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting paged camera bodies", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_GETTING_CAMERAS, e.message))
    }
}

override suspend fun getUserCamerasAsync(cancellationToken: Job): Result<List<CameraBody>> {
    return try {
        cancellationToken.ensureActive()

        val userCameras = withContext(Dispatchers.IO) {
            context.table<CameraBody>()
                .where { it.isUserCreated } as List<CameraBody>()

        }

        Result.success(userCameras)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting user cameras", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_GETTING_USER_CAMERAS, e.message))
    }
}

override suspend fun updateAsync(cameraBody: CameraBody, cancellationToken: Job): Result<CameraBody> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.updateAsync(cameraBody)
        }

        logger.logInfo("Updated camera body with ID: ${cameraBody.id}")
        Result.success(cameraBody)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error updating camera body: ${cameraBody.id}", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_UPDATING, e.message))
    }
}

override suspend fun deleteAsync(id: Int, cancellationToken: Job): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        val result = withContext(Dispatchers.IO) {
            val cameraBody = context.table<CameraBody>()
                .where { it.id == id }

            if (cameraBody != null) {
                context.deleteAsync(cameraBody)
            } else {
                0
            }
        }

        Result.success(result > 0)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error deleting camera body: $id", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_DELETING, e.message))
    }
}

override suspend fun searchByNameAsync(name: String, cancellationToken: Job): Result<List<CameraBody>> {
    return try {
        cancellationToken.ensureActive()

        if (name.isBlank()) {
            return Result.success(emptyList())
        }

        val cameraBodies = withContext(Dispatchers.IO) {
            val allCameras = context.table<CameraBody>() as List<CameraBody>()
            val normalizedSearch = normalizeName(name)

            allCameras
                .filter { isFuzzyMatch(normalizeName(it.name), normalizedSearch) }
                .sortedWith(compareBy({ if (it.isUserCreated) 0 else 1 }, { it.name }))
        }

        Result.success(cameraBodies)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error searching camera bodies by name: $name", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_SEARCHING, e.message))
    }
}

override suspend fun getByMountTypeAsync(mountType: MountType, cancellationToken: Job): Result<List<CameraBody>> {
    return try {
        cancellationToken.ensureActive()

        val cameraBodies = withContext(Dispatchers.IO) {
            context.table<CameraBody>()
                .where { it.mountType == mountType }
               as List<CameraBody>()
        }

        Result.success(cameraBodies)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting camera bodies by mount type: $mountType", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_GETTING_CAMERAS, e.message))
    }
}

override suspend fun getTotalCountAsync(cancellationToken: Job): Result<Int> {
    return try {
        cancellationToken.ensureActive()

        val count = withContext(Dispatchers.IO) {
            context.table<CameraBody>() as List<CameraBody>()
        }.size

        Result.success(count)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting total camera body count", e)
        Result.failure(String.format(CAMERA_BODY_ERROR_GETTING_COUNT, e.message))
    }
}

private fun normalizeName(name: String): String {
    return name.lowercase()
        .replace(" ", "")
        .replace("-", "")
        .replace("_", "")
}

private fun isFuzzyMatch(normalized1: String, normalized2: String): Boolean {
    return normalized1.contains(normalized2) || normalized2.contains(normalized1)
}
}