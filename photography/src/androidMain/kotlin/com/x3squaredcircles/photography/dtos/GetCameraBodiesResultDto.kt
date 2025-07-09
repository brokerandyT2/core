// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/GetCameraBodiesResultDto.kt
package com.x3squaredcircles.photography.dtos

data class GetCameraBodiesResultDto(
    val cameraBodies: List<CameraBodyDto> = emptyList(),
    val totalCount: Int = 0,
    val hasMore: Boolean = false
)