// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/PhoneCameraProfile.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class PhoneCameraProfile private constructor() : Entity() {
    private var _id: Int = 0
    private var _phoneModel: String = ""
    private var _mainLensFocalLength: Double = 0.0
    private var _mainLensFOV: Double = 0.0
    private var _ultraWideFocalLength: Double? = null
    private var _telephotoFocalLength: Double? = null
    private var _dateCalibrated: Long = 0L
    private var _isActive: Boolean = true

    override val id: Int
        get() = _id
    val phoneModel: String
        get() = _phoneModel
    val mainLensFocalLength: Double
        get() = _mainLensFocalLength
    val mainLensFOV: Double
        get() = _mainLensFOV
    val ultraWideFocalLength: Double?
        get() = _ultraWideFocalLength
    val telephotoFocalLength: Double?
        get() = _telephotoFocalLength
    val dateCalibrated: Long
        get() = _dateCalibrated
    val isActive: Boolean
        get() = _isActive

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
    }

    private fun setPhoneModel(value: String) {
        require(value.isNotBlank()) { "Phone model cannot be empty" }
        _phoneModel = value
    }

    private fun setMainLensFocalLength(value: Double) {
        require(value > 0) { "Main lens focal length must be positive" }
        _mainLensFocalLength = value
    }

    private fun setMainLensFOV(value: Double) {
        require(value > 0 && value <= 180) { "Main lens FOV must be between 0 and 180 degrees" }
        _mainLensFOV = value
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

    fun activate() {
        _isActive = true
    }

    fun deactivate() {
        _isActive = false
    }
}
