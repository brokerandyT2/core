Looking at the .NET project knowledge, I can see references to Lens entities in the DatabaseContext. Based on the photography domain context, here's the missing domain entity:
// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Lens.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.Entity

class Lens private constructor() : Entity() {
    private var _id: Int = 0
    private var _name: String = ""
    private var _manufacturer: String = ""
    private var _model: String = ""
    private var _minMM: Double = 0.0
    private var _maxMM: Double = 0.0
    private var _maxAperture: Double = 0.0
    private var _minAperture: Double = 0.0
    private var _isUserCreated: Boolean = false
    private var _mountType: String = ""
    private var _isZoom: Boolean = false
    private var _imageStabilization: Boolean = false
    private var _macroCapable: Boolean = false
    private var _filterSize: Int? = null

    override val id: Int get() = _id
    val name: String get() = _name
    val manufacturer: String get() = _manufacturer
    val model: String get() = _model
    val minMM: Double get() = _minMM
    val maxMM: Double get() = _maxMM
    val maxAperture: Double get() = _maxAperture
    val minAperture: Double get() = _minAperture
    val isUserCreated: Boolean get() = _isUserCreated
    val mountType: String get() = _mountType
    val isZoom: Boolean get() = _isZoom
    val imageStabilization: Boolean get() = _imageStabilization
    val macroCapable: Boolean get() = _macroCapable
    val filterSize: Int? get() = _filterSize

    constructor(
        name: String,
        manufacturer: String,
        model: String,
        minMM: Double,
        maxMM: Double,
        maxAperture: Double,
        mountType: String,
        isUserCreated: Boolean = false
    ) : this() {
        setName(name)
        setManufacturer(manufacturer)
        setModel(model)
        setFocalLength(minMM, maxMM)
        setMaxAperture(maxAperture)
        setMountType(mountType)
        _isUserCreated = isUserCreated
        _isZoom = minMM != maxMM
        _minAperture = maxAperture * 2.0
    }

    private fun setName(value: String) {
        require(value.isNotBlank()) { "Name cannot be empty" }
        _name = value
    }

    private fun setManufacturer(value: String) {
        require(value.isNotBlank()) { "Manufacturer cannot be empty" }
        _manufacturer = value
    }

    private fun setModel(value: String) {
        require(value.isNotBlank()) { "Model cannot be empty" }
        _model = value
    }

    private fun setFocalLength(minMM: Double, maxMM: Double) {
        require(minMM > 0) { "Minimum focal length must be positive" }
        require(maxMM >= minMM) { "Maximum focal length must be greater than or equal to minimum" }
        _minMM = minMM
        _maxMM = maxMM
    }

    private fun setMaxAperture(value: Double) {
        require(value > 0 && value <= 32.0) { "Max aperture must be between 0 and 32" }
        _maxAperture = value
    }

    private fun setMountType(value: String) {
        require(value.isNotBlank()) { "Mount type cannot be empty" }
        _mountType = value
    }

    fun updateSpecs(
        minAperture: Double,
        imageStabilization: Boolean,
        macroCapable: Boolean,
        filterSize: Int?
    ) {
        _minAperture = maxOf(_maxAperture, minAperture)
        _imageStabilization = imageStabilization
        _macroCapable = macroCapable
        _filterSize = filterSize?.let { maxOf(1, it) }
    }

    fun getFocalLengthDescription(): String {
        return if (_isZoom) {
            "${_minMM.toInt()}-${_maxMM.toInt()}mm"
        } else {
            "${_minMM.toInt()}mm"
        }
    }

    fun getFullLensName(): String {
        return "$manufacturer $model ${getFocalLengthDescription()}"
    }

    fun getApertureRange(): String {
        return "f/${_maxAperture}-${_minAperture}"
    }
}