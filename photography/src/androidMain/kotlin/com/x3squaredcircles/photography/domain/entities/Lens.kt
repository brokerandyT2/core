// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Lens.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlin.math.abs

class Lens private constructor() : Entity() {
    private var _id: Int = 0
    private var _minMM: Double = 0.0
    private var _maxMM: Double? = null
    private var _minFStop: Double? = null
    private var _maxFStop: Double? = null
    private var _isPrime: Boolean = false
    private var _isUserCreated: Boolean = false
    private var _dateAdded: Long = 0L
    private var _nameForLens: String = ""

    override val id: Int get() = _id
    val minMM: Double get() = _minMM
    val maxMM: Double? get() = _maxMM
    val minFStop: Double? get() = _minFStop
    val maxFStop: Double? get() = _maxFStop
    val isPrime: Boolean get() = _isPrime
    val isUserCreated: Boolean get() = _isUserCreated
    val dateAdded: Long get() = _dateAdded
    val nameForLens: String get() = _nameForLens

    constructor(
        minMM: Double,
        maxMM: Double? = null,
        minFStop: Double? = null,
        maxFStop: Double? = null,
        isUserCreated: Boolean = false,
        nameForLens: String? = null
    ) : this() {
        setFocalLength(minMM, maxMM)
        setAperture(minFStop, maxFStop)
        _isUserCreated = isUserCreated
        _nameForLens = nameForLens ?: ""
        _isPrime = calculateIsPrime(minMM, maxMM)
        _dateAdded = System.currentTimeMillis()
    }

    private fun setFocalLength(minMM: Double, maxMM: Double?) {
        require(minMM > 0) { "Minimum focal length must be positive" }
        if (maxMM != null) {
            require(maxMM > minMM) { "Maximum focal length must be greater than minimum" }
        }
        _minMM = minMM
        _maxMM = maxMM
    }

    private fun setAperture(minFStop: Double?, maxFStop: Double?) {
        if (minFStop != null) {
            require(minFStop > 0) { "Minimum f-stop must be positive" }
        }
        if (maxFStop != null) {
            require(maxFStop > 0) { "Maximum f-stop must be positive" }
        }
        if (minFStop != null && maxFStop != null) {
            require(maxFStop >= minFStop) { "Maximum f-stop must be greater than or equal to minimum f-stop" }
        }
        _minFStop = minFStop
        _maxFStop = maxFStop
    }

    private fun calculateIsPrime(minMM: Double, maxMM: Double?): Boolean {
        return maxMM == null || abs(maxMM - minMM) < 0.1
    }

    fun updateDetails(
        minMM: Double,
        maxMM: Double? = null,
        minFStop: Double? = null,
        maxFStop: Double? = null
    ) {
        setFocalLength(minMM, maxMM)
        setAperture(minFStop, maxFStop)
        _isPrime = calculateIsPrime(minMM, maxMM)
    }

    fun getDisplayName(): String {
        val focalLengthPart = if (_isPrime) {
            "${_minMM.toInt()}mm"
        } else {
            "${_minMM.toInt()}-${_maxMM?.toInt()}mm"
        }
        
        val aperturePart = getApertureDisplay()
        val displayName = if (aperturePart.isNotEmpty()) {
            "$focalLengthPart $aperturePart"
        } else {
            focalLengthPart
        }.trim()
        
        return if (_isUserCreated) "${displayName}*" else displayName
    }

    private fun getApertureDisplay(): String {
        if (_minFStop == null) return ""

        return if (_maxFStop == null || abs(_maxFStop!! - _minFStop!!) < 0.1) {
            "f/${String.format("%.1f", _minFStop)}"
        } else {
            "f/${String.format("%.1f", _minFStop)}-${String.format("%.1f", _maxFStop)}"
        }
    }

    fun getFocalLengthDescription(): String {
        return if (_isPrime) {
            "${_minMM.toInt()}mm"
        } else {
            "${_minMM.toInt()}-${_maxMM?.toInt()}mm"
        }
    }

    fun getApertureRange(): String {
        return when {
            _minFStop == null -> "Unknown aperture"
            _maxFStop == null || abs(_maxFStop!! - _minFStop!!) < 0.1 -> "f/${String.format("%.1f", _minFStop)}"
            else -> "f/${String.format("%.1f", _minFStop)}-${String.format("%.1f", _maxFStop)}"
        }
    }

    fun isWideAngle(): Boolean {
        return _minMM < 35.0
    }

    fun isStandard(): Boolean {
        return _minMM >= 35.0 && _minMM <= 85.0
    }

    fun isTelephoto(): Boolean {
        return _minMM > 85.0
    }

    fun covers35mmEquivalent(focalLength: Double): Boolean {
        return if (_isPrime) {
            abs(_minMM - focalLength) <= 5.0
        } else {
            _maxMM?.let { focalLength >= _minMM && focalLength <= it } ?: false
        }
    }
}