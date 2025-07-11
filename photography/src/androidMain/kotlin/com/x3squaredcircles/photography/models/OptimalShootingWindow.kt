// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/models/OptimalShootingWindow.kt
package com.x3squaredcircles.photography.models


import kotlinx.datetime.LocalDateTime
import com.x3squaredcircles.photography.models.LightQuality

data class OptimalShootingWindow(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val lightQuality: LightQuality,
    val optimalityScore: Double,
    val description: String = "",
    val recommendedFor: List<String> = emptyList(),
    val name: String = description,
    val quality: LightQuality = lightQuality,
    val lightConditions: String = description
)