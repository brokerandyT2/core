// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/models/FieldOfView.kt
package com.x3squaredcircles.photography.application.common.models

data class CameraInfo(
    val name: String = "",
    val sensor: SensorInfo = SensorInfo(),
    val lenses: List<LensInfo> = emptyList()
)

data class SensorInfo(
    val width: Double = 0.0,
    val height: Double = 0.0
)

data class LensInfo(
    val focalLength: Double = 0.0,
    val maxAperture: Double? = null
)

data class CameraDatabase(
    val cameras: Map<String, CameraData> = emptyMap()
)

data class CameraData(
    val sensor: SensorInfo = SensorInfo(),
    val lenses: List<LensInfo> = emptyList()
)