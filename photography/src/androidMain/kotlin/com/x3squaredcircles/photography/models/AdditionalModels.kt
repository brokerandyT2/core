// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/models/AdditionalModels.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.LocalDateTime

enum class LightQuality {
    Unknown,
    Harsh,
    Soft,
    GoldenHour,
    BlueHour,
    Overcast,
    Dramatic,
    Night,
    Flat,
    Direct
}

data class HourlyLightPrediction(
    val dateTime: LocalDateTime,
    val predictedEV: Double,
    val evConfidenceMargin: Double,
    val confidenceLevel: Double,
    val suggestedSettings: ExposureTriangle = ExposureTriangle(),
    val lightQuality: LightCharacteristics = LightCharacteristics(),
    val recommendations: List<String> = emptyList(),
    val isOptimalForPhotography: Boolean = false,
    val sunPosition: SunPositionDto = SunPositionDto()
)

data class PredictiveLightRecommendation(
    val generatedAt: LocalDateTime,
    val bestTimeWindow: OptimalShootingWindow,
    val alternativeWindows: List<OptimalShootingWindow> = emptyList(),
    val overallRecommendation: String = "",
    val keyInsights: List<String> = emptyList(),
    val calibrationAccuracy: Double = 0.0,
    val requiresRecalibration: Boolean = false
)

data class ExposureTriangle(
    val aperture: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val formattedSettings: String = "$aperture, $shutterSpeed, $iso"
)

data class LightCharacteristics(
    val colorTemperature: Double = 0.0,
    val softnessFactor: Double = 0.0,
    val optimalFor: String = ""
)

data class SunPositionDto(
    val azimuth: Double = 0.0,
    val elevation: Double = 0.0,
    val distance: Double = 1.0,
    val isAboveHorizon: Boolean = elevation > 0
)