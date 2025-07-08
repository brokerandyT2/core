// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/LensCameraCompatibility.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class LensCameraCompatibility() : Entity() {
    private var _id: Int = 0
    private var _lensId: Int = 0
    private var _cameraBodyId: Int = 0
    private var _dateAdded: Long = 0L

    override val id: Int get() = _id
    val lensId: Int get() = _lensId
    val cameraBodyId: Int get() = _cameraBodyId
    val dateAdded: Long get() = _dateAdded

    init {
        _dateAdded = System.currentTimeMillis()
    }

    constructor(lensId: Int, cameraBodyId: Int) : this() {
        require(lensId > 0) { "Lens ID must be positive" }
        require(cameraBodyId > 0) { "Camera body ID must be positive" }
        
        _lensId = lensId
        _cameraBodyId = cameraBodyId
        _dateAdded = System.currentTimeMillis()
    }

    fun updateLensId(lensId: Int) {
        require(lensId > 0) { "Lens ID must be positive" }
        _lensId = lensId
    }

    fun updateCameraBodyId(cameraBodyId: Int) {
        require(cameraBodyId > 0) { "Camera body ID must be positive" }
        _cameraBodyId = cameraBodyId
    }

    fun isValidCompatibility(): Boolean {
        return _lensId > 0 && _cameraBodyId > 0
    }

    fun getCompatibilityKey(): String {
        return "${_lensId}_${_cameraBodyId}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LensCameraCompatibility) return false
        
        return _lensId == other._lensId && _cameraBodyId == other._cameraBodyId
    }

    override fun hashCode(): Int {
        var result = _lensId.hashCode()
        result = 31 * result + _cameraBodyId.hashCode()
        return result
    }

    override fun toString(): String {
        return "LensCameraCompatibility(id=$_id, lensId=$_lensId, cameraBodyId=$_cameraBodyId, dateAdded=$_dateAdded)"
    }
}