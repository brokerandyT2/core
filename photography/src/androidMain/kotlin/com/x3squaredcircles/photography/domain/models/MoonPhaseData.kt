// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/domain/models/MoonPhaseData.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.LocalDateTime

data class MoonPhaseData(
    val date: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0),
    val phase: Double = 0.0, // 0-1, 0 = new moon, 0.5 = full moon
    val phaseName: String = "", // "New Moon", "Waxing Crescent", etc.
    val illuminationPercentage: Double = 0.0, // 0-100
    val moonRise: LocalDateTime? = null,
    val moonSet: LocalDateTime? = null,
    val position: MoonPosition = MoonPosition(),
    val brightness: Double = 0.0 // Magnitude
)

data class MoonPosition(
    val azimuth: Double = 0.0,
    val elevation: Double = 0.0,
    val distance: Double = 384400.0, // km (average distance to moon)
    val isAboveHorizon: Boolean = false
)