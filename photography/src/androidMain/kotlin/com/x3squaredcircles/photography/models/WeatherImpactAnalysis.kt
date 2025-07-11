// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/models/WeatherImpactAnalysis.kt
package com.x3squaredcircles.photography.models

import com.x3squaredcircles.photography.domain.models.WeatherConditions
import com.x3squaredcircles.photography.models.LightQuality


import kotlinx.datetime.LocalDateTime

data class WeatherImpactAnalysis(
    val currentConditions: WeatherConditions? = null,
    val hourlyImpacts: List<HourlyWeatherImpact> = emptyList(),
    val overallLightReductionFactor: Double = 1.0,
    val summary: String = "",
    val alerts: List<WeatherAlert> = emptyList()
)

data class HourlyWeatherImpact(
    val hour: LocalDateTime,
    val lightReductionFactor: Double = 1.0,
    val colorTemperatureShift: Double = 0.0,
    val contrastReduction: Double = 0.0,
    val predictedQuality: LightQuality = LightQuality.Unknown,
    val reasoning: String = ""
)

data class WeatherAlert(
    val type: AlertType,
    val message: String = "",
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val severity: AlertSeverity
)

enum class AlertType {
    SEVERE_WEATHER,
    POOR_VISIBILITY,
    HIGH_WINDS,
    PRECIPITATION,
    EXTREME_TEMPERATURES,
    AIR_QUALITY,
    EQUIPMENT_WARNING
}

enum class AlertSeverity {
    LOW,
    MODERATE,
    HIGH,
    SEVERE
}