// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ExposureTriangleService.kt
package com.x3squaredcircles.photography.infrastructure.services
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.application.services.ShutterSpeeds
import com.x3squaredcircles.photography.application.services.Apertures
import com.x3squaredcircles.photography.application.services.ISOs
import kotlin.math.*
class ExposureTriangleService(
private val loggingService: ILoggingService
) : IExposureTriangleService {
companion object {
    private const val LOG2 = 0.6931471805599453
}

override fun calculateShutterSpeed(
    baseShutterSpeed: String,
    baseAperture: String,
    baseIso: String,
    targetAperture: String,
    targetIso: String,
    scale: Int,
    evCompensation: Double
): String {
    val baseShutterValue = parseShutterSpeed(baseShutterSpeed)
    val baseApertureValue = parseAperture(baseAperture)
    val baseIsoValue = parseIso(baseIso)
    val targetApertureValue = parseAperture(targetAperture)
    val targetIsoValue = parseIso(targetIso)

    val apertureEvDiff = 2 * ln(targetApertureValue / baseApertureValue) / LOG2
    val isoEvDiff = ln(baseIsoValue / targetIsoValue) / LOG2
    val evDiff = apertureEvDiff + isoEvDiff + evCompensation

    val newShutterValue = baseShutterValue * 2.0.pow(evDiff)
    val shutterSpeeds = getShutterSpeedScale(scale)

    val newShutterSpeed = findClosestValue(shutterSpeeds, newShutterValue, ValueType.SHUTTER)

    val maxShutterValue = 30.0
    val minShutterValue = 1.0 / 8000.0

    if (newShutterValue > maxShutterValue * 1.5) {
        val stopsOver = ln(newShutterValue / maxShutterValue) / LOG2
        throw RuntimeException("Overexposed by $stopsOver stops")
    } else if (newShutterValue < minShutterValue / 1.5) {
        val stopsUnder = ln(minShutterValue / newShutterValue) / LOG2
        throw RuntimeException("Underexposed by $stopsUnder stops")
    }

    return newShutterSpeed
}

override fun calculateAperture(
    baseShutterSpeed: String,
    baseAperture: String,
    baseIso: String,
    targetShutterSpeed: String,
    targetIso: String,
    scale: Int,
    evCompensation: Double
): String {
    val baseShutterValue = parseShutterSpeed(baseShutterSpeed)
    val baseApertureValue = parseAperture(baseAperture)
    val baseIsoValue = parseIso(baseIso)
    val targetShutterValue = parseShutterSpeed(targetShutterSpeed)
    val targetIsoValue = parseIso(targetIso)

    val shutterEvDiff = ln(targetShutterValue / baseShutterValue) / LOG2
    val isoEvDiff = ln(targetIsoValue / baseIsoValue) / LOG2
    val evDiff = shutterEvDiff + isoEvDiff - evCompensation

    val newApertureValue = baseApertureValue * sqrt(2.0).pow(evDiff)
    val apertures = getApertureScale(scale)

    val newAperture = findClosestValue(apertures, newApertureValue, ValueType.APERTURE)

    val minApertureValue = parseAperture(apertures[0])

    if (newApertureValue < minApertureValue * 0.7 && scale == 1) {
        val stopsUnder = 2 * ln(minApertureValue / newApertureValue) / LOG2
        throw RuntimeException("Underexposed by $stopsUnder stops")
    }

    return newAperture
}

override fun calculateIso(
    baseShutterSpeed: String,
    baseAperture: String,
    baseIso: String,
    targetShutterSpeed: String,
    targetAperture: String,
    scale: Int,
    evCompensation: Double
): String {
    val baseShutterValue = parseShutterSpeed(baseShutterSpeed)
    val baseApertureValue = parseAperture(baseAperture)
    val baseIsoValue = parseIso(baseIso)
    val targetShutterValue = parseShutterSpeed(targetShutterSpeed)
    val targetApertureValue = parseAperture(targetAperture)

    val shutterEvDiff = ln(baseShutterValue / targetShutterValue) / LOG2
    val apertureEvDiff = 2 * ln(targetApertureValue / baseApertureValue) / LOG2
    val evDiff = shutterEvDiff + apertureEvDiff - evCompensation

    val newIsoValue = baseIsoValue * 2.0.pow(evDiff)
    val isoValues = getIsoScale(scale)

    val newIso = findClosestValue(isoValues, newIsoValue, ValueType.ISO)

    val maxIsoValue = parseIso(isoValues[isoValues.size - 1])
    val minIsoValue = parseIso(isoValues[0])

    if (newIsoValue > maxIsoValue * 1.5) {
        throw RuntimeException("ISO $newIsoValue exceeds maximum $maxIsoValue")
    } else if (newIsoValue < minIsoValue * 0.67) {
        throw RuntimeException("ISO $newIsoValue below minimum $minIsoValue")
    }

    return newIso
}

private fun parseShutterSpeed(shutterSpeed: String): Double {
    if (shutterSpeed.isBlank()) return 0.0

    return when {
        shutterSpeed.contains("/") -> {
            val parts = shutterSpeed.split("/")
            if (parts.size == 2) {
                val numerator = parts[0].toDoubleOrNull() ?: 0.0
                val denominator = parts[1].toDoubleOrNull() ?: 1.0
                if (denominator != 0.0) numerator / denominator else 0.0
            } else 0.0
        }
        shutterSpeed.endsWith("\"") -> {
            val value = shutterSpeed.trimEnd('\"')
            value.toDoubleOrNull() ?: 0.0
        }
        else -> shutterSpeed.toDoubleOrNull() ?: 0.0
    }
}

private fun parseAperture(aperture: String): Double {
    if (aperture.isBlank()) return 0.0

    return when {
        aperture.startsWith("f/") -> {
            val value = aperture.substring(2)
            value.toDoubleOrNull() ?: 0.0
        }
        else -> aperture.toDoubleOrNull() ?: 0.0
    }
}

private fun parseIso(iso: String): Double {
    if (iso.isBlank()) return 0.0
    return iso.toDoubleOrNull() ?: 0.0
}

private fun getShutterSpeedScale(scale: Int): Array<String> {
    return when (scale) {
        1 -> ShutterSpeeds.Full
        2 -> ShutterSpeeds.Halves
        3 -> ShutterSpeeds.Thirds
        else -> ShutterSpeeds.Full
    }
}

private fun getApertureScale(scale: Int): Array<String> {
    return when (scale) {
        1 -> Apertures.Full
        2 -> Apertures.Halves
        3 -> Apertures.Thirds
        else -> Apertures.Full
    }
}

private fun getIsoScale(scale: Int): Array<String> {
    return when (scale) {
        1 -> ISOs.Full
        2 -> ISOs.Halves
        3 -> ISOs.Thirds
        else -> ISOs.Full
    }
}

private fun findClosestValue(values: Array<String>, targetValue: Double, valueType: ValueType): String {
    var closestValue = values[0]
    var closestDifference = Double.MAX_VALUE

    for (value in values) {
        val parsedValue = when (valueType) {
            ValueType.SHUTTER -> parseShutterSpeed(value)
            ValueType.APERTURE -> parseAperture(value)
            ValueType.ISO -> parseIso(value)
        }

        val difference = abs(parsedValue - targetValue)
        if (difference < closestDifference) {
            closestDifference = difference
            closestValue = value
        }
    }

    return closestValue
}

private enum class ValueType {
    SHUTTER,
    APERTURE,
    ISO
}
}