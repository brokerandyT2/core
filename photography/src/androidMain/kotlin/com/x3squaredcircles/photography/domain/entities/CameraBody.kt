// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/CameraBody.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.photography.domain.enums.MountType


class CameraBody private constructor() : Entity() {
    private var _id: Int = 0
    private var _name: String = ""
    private var _sensorType: String = ""
    private var _sensorWidth: Double = 0.0
    private var _sensorHeight: Double = 0.0
    private var _mountType: MountType = MountType.Other
    private var _isUserCreated: Boolean = false
    private var _dateAdded: Long = 0L

    override val id: Int get() = _id
    val name: String get() = _name
    val sensorType: String get() = _sensorType
    val sensorWidth: Double get() = _sensorWidth
    val sensorHeight: Double get() = _sensorHeight
    val mountType: MountType get() = _mountType
    val isUserCreated: Boolean get() = _isUserCreated
    val dateAdded: Long get() = _dateAdded

    constructor(
        name: String,
        sensorType: String,
        sensorWidth: Double,
        sensorHeight: Double,
        mountType: MountType,
        isUserCreated: Boolean = false
    ) : this() {
        setName(name)
        setSensorType(sensorType)
        setSensorDimensions(sensorWidth, sensorHeight)
        _mountType = mountType
        _isUserCreated = isUserCreated
        _dateAdded = System.currentTimeMillis()
    }

    private fun setName(value: String) {
        require(value.isNotBlank()) { "Camera name cannot be null or empty" }
        _name = value.trim()
    }

    private fun setSensorType(value: String) {
        require(value.isNotBlank()) { "Sensor type cannot be null or empty" }
        _sensorType = value.trim()
    }

    private fun setSensorDimensions(width: Double, height: Double) {
        require(width > 0) { "Sensor width must be positive" }
        require(height > 0) { "Sensor height must be positive" }
        _sensorWidth = width
        _sensorHeight = height
    }

    fun updateDetails(
        name: String,
        sensorType: String,
        sensorWidth: Double,
        sensorHeight: Double,
        mountType: MountType
    ) {
        setName(name)
        setSensorType(sensorType)
        setSensorDimensions(sensorWidth, sensorHeight)
        _mountType = mountType
    }

    fun getDisplayName(): String {
        return if (_isUserCreated) "${_name}*" else _name
    }

    fun getSensorDiagonal(): Double {
        return kotlin.math.sqrt(_sensorWidth * _sensorWidth + _sensorHeight * _sensorHeight)
    }

    fun getCropFactor(): Double {
        val fullFrameDiagonal = kotlin.math.sqrt(36.0 * 36.0 + 24.0 * 24.0)
        return fullFrameDiagonal / getSensorDiagonal()
    }

    fun isFullFrame(): Boolean {
        return getCropFactor() <= 1.1
    }
}