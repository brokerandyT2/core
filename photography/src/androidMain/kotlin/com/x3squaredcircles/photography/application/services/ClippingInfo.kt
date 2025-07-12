// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/ClippingInfo.kt
package com.x3squaredcircles.photography.application.services

/**
 * Clipping detection information for highlights and shadows
 */
data class ClippingInfo(
    val hasHighlightClipping: Boolean,
    val hasShadowClipping: Boolean,
    val highlightClippingPercentage: Double,
    val shadowClippingPercentage: Double
)