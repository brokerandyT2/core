// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/CameraSensorProfileService.kt
package com.x3squaredcircles.photography.infrastructure.services
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.application.commands.cameraevaluation.CameraBodyDto
import com.x3squaredcircles.photography.application.services.ICameraSensorProfileService
import com.x3squaredcircles.photography.domain.enums.MountType
import kotlinx.coroutines.Job
import kotlinx.serialization.json.*
import kotlinx.datetime.Clock
import kotlinx.datetime.*

class CameraSensorProfileService(
private val loggingService: ILoggingService
) : ICameraSensorProfileService {
override suspend fun loadCameraSensorProfilesAsync(
    jsonContents: List<String>,
    cancellationToken: Job
): Result<List<CameraBodyDto>> {
    return try {
        val cameras = mutableListOf<CameraBodyDto>()

        jsonContents.forEach { jsonContent ->
            val fileCameras = parseCameraJsonAsync(jsonContent, cancellationToken)
            if (fileCameras.isSuccess) {
                cameras.addAll(fileCameras.data ?: emptyList())
            }
        }

        loggingService.logInfo("Loaded ${cameras.size} cameras from ${jsonContents.size} JSON contents")
        Result.success(cameras)
    } catch (ex: Exception) {
        loggingService.logError("Error loading camera sensor profiles from JSON contents", ex)
        Result.failure("Error retrieving cameras")
    }
}

private suspend fun parseCameraJsonAsync(
    jsonContent: String,
    cancellationToken: Job
): Result<List<CameraBodyDto>> {
    return try {
        val cameras = mutableListOf<CameraBodyDto>()
        val jsonElement = Json.parseToJsonElement(jsonContent)

        if (jsonElement is JsonObject && jsonElement.containsKey("Cameras")) {
            val camerasElement = jsonElement["Cameras"]?.jsonObject

            camerasElement?.forEach { (cameraName, cameraData) ->
                val cameraObject = cameraData.jsonObject

                val brandElement = cameraObject["Brand"]?.jsonPrimitive?.content
                val sensorTypeElement = cameraObject["SensorType"]?.jsonPrimitive?.content
                val sensorElement = cameraObject["Sensor"]?.jsonObject

                if (brandElement != null && sensorTypeElement != null && sensorElement != null) {
                    val widthElement = sensorElement["SensorWidthInMM"]?.jsonPrimitive?.double
                    val heightElement = sensorElement["SensorHeightInMM"]?.jsonPrimitive?.double

                    if (widthElement != null && heightElement != null) {
                        val mountType = determineMountType(brandElement, cameraName)
                        val kInstant = Clock.System.now()
                        val cameraDto = CameraBodyDto(
                            id = 0,
                            name = cameraName,
                            sensorType = sensorTypeElement.ifEmpty { "Unknown" },
                            sensorWidth = widthElement,
                            sensorHeight = heightElement,
                            mountType = mountType,
                            isUserCreated = false,
                            dateAdded =  kInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                            displayName = cameraName
                        )

                        cameras.add(cameraDto)
                    }
                }
            }
        }

        Result.success(cameras)
    } catch (ex: Exception) {
        loggingService.logError("Error parsing camera JSON content", ex)
        Result.failure("Error parsing camera data")
    }
}

private fun determineMountType(brand: String?, cameraName: String): MountType {
    val brandLower = brand?.lowercase() ?: ""
    val cameraNameLower = cameraName.lowercase()

    return when (brandLower) {
        "canon" -> when {
            cameraNameLower.contains("eos r") -> MountType.CanonRF
            cameraNameLower.contains("eos m") -> MountType.CanonEFM
            else -> MountType.CanonEF
        }
        "nikon" -> when {
            cameraNameLower.contains(" z") -> MountType.NikonZ
            else -> MountType.NikonZ
        }
        "sony" -> when {
            cameraNameLower.contains("fx") || cameraNameLower.contains("a7") -> MountType.SonyFE
            else -> MountType.SonyE
        }
        "fujifilm" -> when {
            cameraNameLower.contains("gfx") -> MountType.FujifilmGFX
            else -> MountType.FujifilmX
        }
        "pentax" -> MountType.PentaxK
        "olympus" -> MountType.MicroFourThirds
        "panasonic" -> MountType.MicroFourThirds
        "leica" -> when {
            cameraNameLower.contains(" sl") -> MountType.LeicaSL
            cameraNameLower.contains(" m") -> MountType.LeicaM
            else -> MountType.LeicaL
        }
        else -> MountType.Other
    }
}
}