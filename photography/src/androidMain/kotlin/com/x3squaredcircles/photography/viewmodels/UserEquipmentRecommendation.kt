// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/UserEquipmentRecommendation.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photography.domain.models.AstroTarget
import kotlinx.datetime.LocalDateTime

data class UserEquipmentRecommendation(
    val target: AstroTarget,
    val recommendedCombinations: List<CameraLensCombination> = emptyList(),
    val alternativeCombinations: List<CameraLensCombination> = emptyList(),
    val summary: String = "",
    val hasOptimalEquipment: Boolean = false,
    val targetSpecs: OptimalEquipmentSpecs = OptimalEquipmentSpecs()
)

data class CameraLensCombination(
    val camera: CameraBody,
    val lens: Lens,
    val matchScore: Double = 0.0, // 0-100 how well this matches target requirements
    val recommendationReason: String = "",
    val strengths: List<String> = emptyList(),
    val limitations: List<String> = emptyList(),
    val isOptimal: Boolean = false,
    val detailedRecommendation: String = ""
) {
    val displayText: String
        get() = "Use ${camera.name} with ${lens.nameForLens}"
}

data class HourlyEquipmentRecommendation(
    val predictionTime: LocalDateTime,
    val target: AstroTarget,
    val recommendedCombination: CameraLensCombination? = null,
    val recommendation: String = "",
    val hasUserEquipment: Boolean = false,
    val genericRecommendation: String = ""
)

data class GenericEquipmentRecommendation(
    val target: AstroTarget,
    val lensRecommendation: String = "",
    val cameraRecommendation: String = "",
    val specs: OptimalEquipmentSpecs = OptimalEquipmentSpecs(),
    val shoppingList: List<String> = emptyList()
)

data class OptimalEquipmentSpecs(
    val minFocalLength: Double = 0.0,
    val maxFocalLength: Double = 0.0,
    val optimalFocalLength: Double = 0.0,
    val maxAperture: Double = 0.0,
    val minISO: Int = 0,
    val maxISO: Int = 0,
    val recommendedSettings: String = "",
    val notes: String = ""
)