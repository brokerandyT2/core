// com/x3squaredcircles/photography/application/services/models/FixedValue.kt
package com.x3squaredcircles.photography.application.services.models

/**
 * Defines which part of the exposure triangle should be calculated
 */
enum class FixedValue(val value: Int) {
    ShutterSpeeds(0),
    ISO(1),
    Empty(2),
    Aperture(3)
}