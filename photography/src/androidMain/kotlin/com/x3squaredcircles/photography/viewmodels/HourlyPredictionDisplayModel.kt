// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/viewmodels/HourlyPredictionDisplayModel.kt
package com.x3squaredcircles.photography.viewmodels

import kotlinx.datetime.LocalDateTime
import java.util.Locale

data class HourlyPredictionDisplayModel(
    val time: LocalDateTime,
    val deviceTimeDisplay: String = "",
    val locationTimeDisplay: String = "",
    val predictedEV: Double = 0.0,
    val evConfidenceMargin: Double = 0.0,
    val suggestedAperture: String = "",
    val suggestedShutterSpeed: String = "",
    val suggestedISO: String = "",
    val confidenceLevel: Double = 0.0,
    val lightQuality: String = "",
    val colorTemperature: Double = 0.0,
    val recommendations: String = "",
    val isOptimalTime: Boolean = false,
    val timeFormat: String = "HH:mm",
    val shootingQualityScore: Double = 0.0,
    val weatherDescription: String = "",
    val cloudCover: Int = 0,
    val precipitationProbability: Double = 0.0,
    val windInfo: String = "",
    val uvIndex: Double = 0.0,
    val humidity: Int = 0,
    val equipmentRecommendation: HourlyEquipmentRecommendation? = null
) {
    val hasUserEquipment: Boolean
        get() = equipmentRecommendation?.hasUserEquipment ?: false
    
    val equipmentRecommendationText: String
        get() = equipmentRecommendation?.recommendation ?: "No equipment recommendation available"
    
    val userCameraLensRecommendation: String
        get() {
            equipmentRecommendation?.recommendedCombination?.let { combo ->
                return "📷 Use ${combo.camera.name} with ${combo.lens.nameForLens}"
            }
            return equipmentRecommendationText
        }
    
    val equipmentMatchScore: String
        get() {
            equipmentRecommendation?.recommendedCombination?.let { combo ->
                val score = combo.matchScore
                return when {
                    score >= 85 -> "Excellent Match"
                    score >= 70 -> "Good Match"
                    score >= 50 -> "Fair Match"
                    else -> "Poor Match"
                }
            }
            return "No Match Available"
        }
    
    val weatherDescriptionCapitalized: String
        get() = weatherDescription.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    
    val formattedPrediction: String
        get() = "EV ${String.format("%.1f", predictedEV)} ±${String.format("%.1f", evConfidenceMargin)}"
    
    val confidenceDisplay: String
        get() = "${(confidenceLevel * 100).toInt()}%"
    
    val fullRecommendation: String
        get() {
            var recommendation = "f/$suggestedAperture @ $suggestedShutterSpeed ISO $suggestedISO"
            if (colorTemperature > 0) {
                recommendation += " • ${String.format("%.0f", colorTemperature)}K"
            }
            return recommendation
        }
    
    val compactSummary: String
        get() = "$formattedPrediction • $confidenceDisplay • $lightQuality"
}


data class CameraInfo(
    val name: String
)

data class LensInfo(
    val nameForLens: String
)