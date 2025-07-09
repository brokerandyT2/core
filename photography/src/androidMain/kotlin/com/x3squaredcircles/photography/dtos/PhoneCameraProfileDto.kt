// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/PhoneCameraProfileDto.kt
package com.x3squaredcircles.photography.dtos

data class PhoneCameraProfileDto(
    val id: Int = 0,
    val phoneModel: String = "",
    val mainLensFocalLength: Double = 0.0,
    val mainLensFOV: Double = 0.0,
    val ultraWideFocalLength: Double? = null,
    val telephotoFocalLength: Double? = null,
    val dateCalibrated: Long = 0L,
    val isActive: Boolean = true,
    val isCalibrationSuccessful: Boolean = false,
    val errorMessage: String = ""
) {
    fun hasMultipleLenses(): Boolean {
        return ultraWideFocalLength != null || telephotoFocalLength != null
    }

    fun getAvailableLenses(): List<Double> {
        val lenses = mutableListOf(mainLensFocalLength)
        ultraWideFocalLength?.let { lenses.add(it) }
        telephotoFocalLength?.let { lenses.add(it) }
        return lenses.sorted()
    }

    fun getLensDescription(): String {
        val lenses = getAvailableLenses()
        return when (lenses.size) {
            1 -> "${lenses[0].toInt()}mm"
            2 -> "${lenses[0].toInt()}mm + ${lenses[1].toInt()}mm"
            3 -> "${lenses[0].toInt()}mm + ${lenses[1].toInt()}mm + ${lenses[2].toInt()}mm"
            else -> "${lenses.size} lenses"
        }
    }

    fun getDisplayName(): String {
        val status = if (isActive) "" else " (Inactive)"
        return "$phoneModel - ${getLensDescription()}$status"
    }

    fun isCalibrationStale(maxAge: Long): Boolean {
        return System.currentTimeMillis() - dateCalibrated > maxAge
    }

    fun isEquivalentTo35mm(focalLength35mm: Double, tolerance: Double = 5.0): Boolean {
        val lenses = getAvailableLenses()
        return lenses.any { kotlin.math.abs(it - focalLength35mm) <= tolerance }
    }
}