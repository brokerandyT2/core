// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/IExposureTriangleService.kt
package com.x3squaredcircles.photography.infrastructure.services
interface IExposureTriangleService {
fun calculateShutterSpeed(
baseShutterSpeed: String,
baseAperture: String,
baseIso: String,
targetAperture: String,
targetIso: String,
scale: Int,
evCompensation: Double = 0.0
): String
fun calculateAperture(
    baseShutterSpeed: String,
    baseAperture: String,
    baseIso: String,
    targetShutterSpeed: String,
    targetIso: String,
    scale: Int,
    evCompensation: Double = 0.0
): String

fun calculateIso(
    baseShutterSpeed: String,
    baseAperture: String,
    baseIso: String,
    targetShutterSpeed: String,
    targetAperture: String,
    scale: Int,
    evCompensation: Double = 0.0
): String
}