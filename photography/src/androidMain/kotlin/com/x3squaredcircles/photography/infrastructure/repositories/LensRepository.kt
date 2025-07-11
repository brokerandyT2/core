Now let me get the complete LensRepository implementation:Searched project for “LensRepository GetCompatibleLensesAsync GetTotalCountAsync complete methods implementation”6 resultsLocation/Location.Photography.Application/Common/Interfaces/ILensRepository.cstextLocation/Location.Photography.Application/Commands/CameraEvaluation/GetLensesQuery.cstextphotography/src/androidMain/kotlin/com/x3squaredcircles/photography/repositories/ILensRepository.kttextLocation/Location.Photography.Infrastructure/Repositories/LensRepository.cs3 relevant sectionstextphotography/src/androidMain/kotlin/com/x3squaredcircles/photography/handlers/GetLensesQueryHandler.kttextLocation/Location.Photography.Infrastructure/DataPopulation/DatabaseInitializer.cstextPerfect! Now I have the complete LensRepository implementation. Let me migrate it to Kotlin following the rules:
// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/LensRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.database.IDatabaseContext
import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility
import com.x3squaredcircles.photography.repositories.ILensRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.math.abs
interface ILogger {
fun logInformation(message: String)
fun logError(message: String, exception: Exception? = null)
}
class LensRepository(
private val context: IDatabaseContext,
private val logger: ILogger
) : ILensRepository {
companion object {
    private const val LENS_ERROR_CANNOT_BE_NULL = "Lens cannot be null"
    private const val LENS_ERROR_NOT_FOUND = "Lens not found"
    private const val LENS_ERROR_CREATING = "Error creating lens: %s"
    private const val LENS_ERROR_GETTING = "Error getting lens: %s"
    private const val LENS_ERROR_GETTING_ALL = "Error getting lenses: %s"
    private const val LENS_ERROR_GETTING_USER = "Error getting user lenses: %s"
    private const val LENS_ERROR_UPDATING = "Error updating lens: %s"
    private const val LENS_ERROR_DELETING = "Error deleting lens: %s"
    private const val LENS_ERROR_SEARCHING = "Error searching lenses: %s"
}

override suspend fun createAsync(lens: Lens, cancellationToken: Job): Result<Lens> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.insertAsync(lens)
        }

        logger.logInformation("Created lens with ID: ${lens.id}")
        Result.success(lens)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error creating lens", e)
        Result.failure(String.format(LENS_ERROR_CREATING, e.message))
    }
}

override suspend fun getByIdAsync(id: Int, cancellationToken: Job): Result<Lens> {
    return try {
        cancellationToken.ensureActive()

        val lens = withContext(Dispatchers.IO) {
            val lenses = context.table<Lens>()
                .where { it.id == id }
               as List<Lens>()
            lenses.firstOrNull()
        }

        if (lens != null) {
            Result.success(lens)
        } else {
            Result.failure(LENS_ERROR_NOT_FOUND)
        }
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting lens by ID: $id", e)
        Result.failure(String.format(LENS_ERROR_GETTING, e.message))
    }
}

override suspend fun getPagedAsync(skip: Int, take: Int, cancellationToken: Job): Result<List<Lens>> {
    return try {
        cancellationToken.ensureActive()

        val lenses = withContext(Dispatchers.IO) {
            val allLenses = context.table<Lens>() as List<Lens>()
            
            allLenses
                .sortedWith(compareBy({ if (it.isUserCreated) 0 else 1 }, { it.minMM }))
                .drop(skip)
                .take(take)
        }

        Result.success(lenses)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting paged lenses", e)
        Result.failure(String.format(LENS_ERROR_GETTING_ALL, e.message))
    }
}

override suspend fun getUserLensesAsync(cancellationToken: Job): Result<List<Lens>> {
    return try {
        cancellationToken.ensureActive()

        val userLenses = withContext(Dispatchers.IO) {
            context.table<Lens>()
                .where { it.isUserCreated }
                as List<Lens>()
        }

        Result.success(userLenses)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting user lenses", e)
        Result.failure(String.format(LENS_ERROR_GETTING_USER, e.message))
    }
}

override suspend fun updateAsync(lens: Lens, cancellationToken: Job): Result<Lens> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.updateAsync(lens)
        }

        logger.logInformation("Updated lens with ID: ${lens.id}")
        Result.success(lens)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error updating lens: ${lens.id}", e)
        Result.failure(String.format(LENS_ERROR_UPDATING, e.message))
    }
}

override suspend fun deleteAsync(id: Int, cancellationToken: Job): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        val result = withContext(Dispatchers.IO) {
            val lens = context.table<Lens>()
                .where { it.id == id }
        

            if (lens != null) {
                context.deleteAsync(lens)
            } else {
                0
            }
        }

        Result.success(result > 0)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error deleting lens: $id", e)
        Result.failure(String.format(LENS_ERROR_DELETING, e.message))
    }
}

override suspend fun searchByFocalLengthAsync(focalLength: Double, cancellationToken: Job): Result<List<Lens>> {
    return try {
        cancellationToken.ensureActive()

        val lenses = withContext(Dispatchers.IO) {
            val allLenses = context.table<Lens>() as List<Lens>()

            allLenses
                .filter { lens ->
                    (lens.isPrime && abs(lens.minMM - focalLength) <= 5) ||
                    (!lens.isPrime && lens.maxMM != null && focalLength >= lens.minMM && focalLength <= lens.maxMM!!)
                }
                .sortedWith(compareBy(
                    { if (it.isUserCreated) 0 else 1 },
                    { abs(it.minMM - focalLength) }
                ))
        }

        Result.success(lenses)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error searching lenses by focal length: $focalLength", e)
        Result.failure(String.format(LENS_ERROR_SEARCHING, e.message))
    }
}

override suspend fun getCompatibleLensesAsync(cameraBodyId: Int, cancellationToken: Job): Result<List<Lens>> {
    return try {
        cancellationToken.ensureActive()

        val compatibleLenses = withContext(Dispatchers.IO) {
            val compatibilities = context.table<LensCameraCompatibility>()
                .where { it.cameraBodyId == cameraBodyId }
                as List<LensCameraCompatibility>()

            val lensIds = compatibilities.map { it.lensId }

            context.table<Lens>()
                .where { lens -> lensIds.contains(lens.id) }
                as List<Lens>()
        }

        Result.success(compatibleLenses)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting compatible lenses for camera: $cameraBodyId", e)
        Result.failure(String.format(LENS_ERROR_GETTING_ALL, e.message))
    }
}

override suspend fun getTotalCountAsync(cancellationToken: Job): Result<Int> {
    return try {
        cancellationToken.ensureActive()

        val count = withContext(Dispatchers.IO) {
            context.table<Lens>() as List<Lens>()
        }.size

        Result.success(count)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting total lens count", e)
        Result.failure(String.format(LENS_ERROR_GETTING_ALL, e.message))
    }
}
}