// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/handlers/GetCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetCameraBodiesQuery
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photography.dtos.GetCameraBodiesResultDto
import com.x3squaredcircles.photography.repositories.ICameraBodyRepository


class GetCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val cameraSensorProfileService: ICameraSensorProfileService
) : IQueryHandler<GetCameraBodiesQuery, Result<GetCameraBodiesResultDto>> {

    override suspend fun handle(request: GetCameraBodiesQuery): Result<GetCameraBodiesResultDto> {
        return try {
            val allCameras = mutableListOf<CameraBodyDto>()

            if (request.userCamerasOnly) {
                // Load only user-created cameras from database
                val userCamerasResult = cameraBodyRepository.getUserCamerasAsync()
                if (!userCamerasResult.isSuccess) {
                    val errorMsg = when (userCamerasResult) {
                        is Result.Failure -> userCamerasResult.errorMessage
                        else -> "Error retrieving user cameras"
                    }
                    return Result.failure(errorMsg)
                }

                val userCameraDtos = userCamerasResult.data?.map { camera ->
                    CameraBodyDto(
                        id = camera.id,
                        name = camera.name,
                        sensorType = camera.sensorType,
                        sensorWidth = camera.sensorWidth,
                        sensorHeight = camera.sensorHeight,
                        mountType = camera.mountType,
                        isUserCreated = camera.isUserCreated,
                        dateAdded = camera.dateAdded,
                        displayName = camera.getDisplayName()
                    )
                } ?: emptyList()

                allCameras.addAll(userCameraDtos)
            } else {
                // Load all database cameras (user + system)
                val allDbCamerasResult = cameraBodyRepository.getPagedAsync(0, Int.MAX_VALUE)
                if (allDbCamerasResult.isSuccess && allDbCamerasResult.data != null) {
                    val dbCameraDtos = allDbCamerasResult.data!!.map { camera ->
                        CameraBodyDto(
                            id = camera.id,
                            name = camera.name,
                            sensorType = camera.sensorType,
                            sensorWidth = camera.sensorWidth,
                            sensorHeight = camera.sensorHeight,
                            mountType = camera.mountType,
                            isUserCreated = camera.isUserCreated,
                            dateAdded = camera.dateAdded,
                            displayName = camera.getDisplayName()
                        )
                    }
                    allCameras.addAll(dbCameraDtos)
                }

                // Load cameras from JSON sensor profiles
                val jsonCamerasResult = cameraSensorProfileService.loadCameraSensorProfilesAsync(emptyList())
                if (jsonCamerasResult.isSuccess && jsonCamerasResult.data != null) {
                    allCameras.addAll(jsonCamerasResult.data!!)
                }
            }

            // Sort cameras (user cameras first, then alphabetically)
            val sortedCameras = allCameras.sortedWith(
                compareBy<CameraBodyDto> { if (it.isUserCreated) 0 else 1 }
                    .thenBy { it.displayName }
            )

            // Apply paging
            val totalCount = sortedCameras.size
            val pagedCameras = sortedCameras.drop(request.skip).take(request.take)

            val result = GetCameraBodiesResultDto(
                cameraBodies = pagedCameras,
                totalCount = totalCount,
                hasMore = (request.skip + request.take) < totalCount
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure("Error retrieving camera bodies: ${e.message}")
        }
    }
}