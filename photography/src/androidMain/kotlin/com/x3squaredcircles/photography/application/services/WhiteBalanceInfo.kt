// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/WhiteBalanceInfo.kt
package com.x3squaredcircles.photography.application.services

/**
 * White balance information extracted from image analysis
 */
data class WhiteBalanceInfo(
    val colorTemperature: Double,
    val tint: Double,
    val confidence: Double
)