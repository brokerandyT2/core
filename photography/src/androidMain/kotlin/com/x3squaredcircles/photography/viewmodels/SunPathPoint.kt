// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/models/SunPathPoint.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.LocalDateTime

data class SunPathPoint(
    val time: LocalDateTime,
    val azimuth: Double,
    val elevation: Double,
    val isVisible: Boolean
)