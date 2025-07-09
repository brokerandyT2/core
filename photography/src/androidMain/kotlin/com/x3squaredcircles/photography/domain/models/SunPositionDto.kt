// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SunPositionDto.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.LocalDateTime

data class SunPositionDto(
    val azimuth: Double = 0.0,
    val elevation: Double = 0.0,
    val dateTime: LocalDateTime,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distance: Double = 0.0
) {
    val isAboveHorizon: Boolean
        get() = elevation > 0
}