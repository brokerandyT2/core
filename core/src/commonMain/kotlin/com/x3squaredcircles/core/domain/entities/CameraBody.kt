// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/CameraBody.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class CameraBody private constructor() : Entity() {
    private var _id: Int = 0
    private var _name: String = ""
    private var _mountType: String = ""
    private var _isUserCreated: Boolean = false
    private var _manufacturer: String = ""
    private var _model: String = ""
    private var _releaseYear: Int? = null
    private var _cropFactor: Double = 1.0
    private var _megapixels: Double? = null
    private var _maxIso: Int? = null
    private var _imageStabilization: Boolean = false

    override val id: Int
        get() = _id
    val name: String
        get() = _name
    val mountType: String
        get() = _mountType
    val isUserCreated: Boolean
        get() = _isUserCreated
    val manufacturer: String
        get() = _manufacturer
    val model: String
        get() = _model
    val releaseYear: Int?
        get() = _releaseYear
    val cropFactor: Double
        get() = _cropFactor
    val megapixels: Double?
        get() = _megapixels
    val maxIso: Int?
        get() = _maxIso
    val imageStabilization: Boolean
        get() = _imageStabilization

    constructor(
            name: String,
            mountType: String,
            manufacturer: String,
            model: String,
            isUserCreated: Boolean = false
    ) : this() {
        setName(name)
        setMountType(mountType)
        setManufacturer(manufacturer)
        setModel(model)
        _isUserCreated = isUserCreated
    }

    private fun setName(value: String) {
        require(value.isNotBlank()) { "Name cannot be empty" }
        _name = value
    }

    private fun setMountType(value: String) {
        require(value.isNotBlank()) { "Mount type cannot be empty" }
        _mountType = value
    }

    private fun setManufacturer(value: String) {
        require(value.isNotBlank()) { "Manufacturer cannot be empty" }
        _manufacturer = value
    }

    private fun setModel(value: String) {
        require(value.isNotBlank()) { "Model cannot be empty" }
        _model = value
    }

    fun updateSpecs(
            releaseYear: Int?,
            cropFactor: Double,
            megapixels: Double?,
            maxIso: Int?,
            imageStabilization: Boolean
    ) {
        _releaseYear = releaseYear
        _cropFactor = maxOf(0.1, cropFactor)
        _megapixels = megapixels?.let { maxOf(0.0, it) }
        _maxIso = maxIso?.let { maxOf(50, it) }
        _imageStabilization = imageStabilization
    }

    fun getFullCameraName(): String {
        return "$manufacturer $model"
    }

    fun isFullFrame(): Boolean {
        return cropFactor <= 1.1
    }
}
