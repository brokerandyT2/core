// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/PhoneCameraProfile.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class PhoneCameraProfile() : Entity() {
    private var _id: Int = 0
    private var _phoneModel: String = ""
    private var _mainLensFocalLength: Double = 0.0
    private var _mainLensFOV: Double = 0.0
    private var _ultraWideFocalLength: Double? = null
    private var _telephotoFocalLength: Double? = null
    private var _dateCalibrated: Long = 0L
    private var _isActive: Boolean = true

    override val id: Int get() = _id
    val phoneModel: String get() = _phoneModel
    val mainLensFocalLength: Double get() = _mainLensFocalLength
    val mainLensFOV: Double get() = _mainLensFOV
    val ultraWideFocalLength: Double? get() = _ultraWideFocalLength
    val telephotoFocalLength: Double? get() = _telephotoFocalLength
    val dateCalibrated: Long get() = _dateCalibrated
    val isActive: Boolean get() = _isActive

    constructor(
        phoneModel: String,
        mainLensFocalLength: Double,
        mainLensFOV: Double,
        ultraWideFocalLength: Double? = null,
        telephotoFocalLength: Double? = null
    ) : this() {
        setPhoneModel(phoneModel)
        setMainLensFocalLength(mainLensFocalLength)
        setMainLensFOV(mainLensFOV)
        _ultraWideFocalLength = ultraWideFocalLength
        _telephotoFocalLength = telephotoFocalLength
        _dateCalibrated = System.currentTimeMillis()
        _isActive = true
    }

    private fun setPhoneModel(value: String) {
        require(value.isNotBlank()) { "Phone model cannot be null or empty" }
        _phoneModel = value.trim()
    }

    private fun setMainLensFocalLength(value: Double) {
        require(value > 0) { "Main lens focal length must be positive" }
        _mainLensFocalLength = value
    }

    private fun setMainLensFOV(value: Double) {
        require(value > 0 && value < 180) { "Main lens FOV must be between 0 and 180 degrees" }
        _mainLensFOV = value
    }

    fun deactivate() {
        _isActive = false
    }

    fun activate() {
        _isActive = true
    }

    fun isCalibrationStale(maxAge: Long): Boolean {
        return System.currentTimeMillis() - _dateCalibrated > maxAge
    }

    fun updateLensData(
        mainLensFocalLength: Double,
        mainLensFOV: Double,
        ultraWideFocalLength: Double? = null,
        telephotoFocalLength: Double? = null
    ) {
        setMainLensFocalLength(mainLensFocalLength)
        setMainLensFOV(mainLensFOV)
        _ultraWideFocalLength = ultraWideFocalLength
        _telephotoFocalLength = telephotoFocalLength
        _dateCalibrated = System.currentTimeMillis()
    }

    fun hasMultipleLenses(): Boolean {
        return _ultraWideFocalLength != null || _telephotoFocalLength != null
    }

    fun getAvailableLenses(): List<Double> {
        val lenses = mutableListOf(_mainLensFocalLength)
        _ultraWideFocalLength?.let { lenses.add(it) }
        _telephotoFocalLength?.let { lenses.add(it) }
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
        val status = if (_isActive) "" else " (Inactive)"
        return "${_phoneModel} - ${getLensDescription()}$status"
    }

    fun isEquivalentTo35mm(focalLength35mm: Double, tolerance: Double = 5.0): Boolean {
        val lenses = getAvailableLenses()
        return lenses.any { kotlin.math.abs(it - focalLength35mm) <= tolerance }
    }

    override fun toString(): String {
        return "PhoneCameraProfile(phoneModel='$_phoneModel', mainFocalLength=${_mainLensFocalLength}mm, mainFOV=${_mainLensFOV}°, isActive=$_isActive)"
    }
}