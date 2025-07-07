// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/LensCameraCompatibility.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class LensCameraCompatibility private constructor() : Entity() {
    private var _id: Int = 0
    private var _lensId: Int = 0
    private var _cameraBodyId: Int = 0
    private var _isNativeMount: Boolean = false
    private var _requiresAdapter: Boolean = false
    private var _adapterType: String? = null
    private var _autofocusSupported: Boolean = true
    private var _imageStabilizationSupported: Boolean = true
    private var _notes: String = ""

    override val id: Int
        get() = _id
    val lensId: Int
        get() = _lensId
    val cameraBodyId: Int
        get() = _cameraBodyId
    val isNativeMount: Boolean
        get() = _isNativeMount
    val requiresAdapter: Boolean
        get() = _requiresAdapter
    val adapterType: String?
        get() = _adapterType
    val autofocusSupported: Boolean
        get() = _autofocusSupported
    val imageStabilizationSupported: Boolean
        get() = _imageStabilizationSupported
    val notes: String
        get() = _notes

    constructor(lensId: Int, cameraBodyId: Int, isNativeMount: Boolean = true) : this() {
        require(lensId > 0) { "Lens ID must be positive" }
        require(cameraBodyId > 0) { "Camera body ID must be positive" }

        _lensId = lensId
        _cameraBodyId = cameraBodyId
        _isNativeMount = isNativeMount
        _requiresAdapter = !isNativeMount
        _autofocusSupported = isNativeMount
        _imageStabilizationSupported = isNativeMount
    }

    fun setAdapterRequirement(
            requiresAdapter: Boolean,
            adapterType: String? = null,
            autofocusSupported: Boolean = false,
            imageStabilizationSupported: Boolean = false
    ) {
        _requiresAdapter = requiresAdapter
        _adapterType = if (requiresAdapter) adapterType else null
        _autofocusSupported = if (requiresAdapter) autofocusSupported else true
        _imageStabilizationSupported = if (requiresAdapter) imageStabilizationSupported else true
    }

    fun addNotes(notes: String) {
        _notes = notes
    }

    fun isFullyCompatible(): Boolean {
        return _isNativeMount && _autofocusSupported && _imageStabilizationSupported
    }

    fun getCompatibilityDescription(): String {
        return when {
            _isNativeMount -> "Native mount - fully compatible"
            _requiresAdapter && _autofocusSupported ->
                    "Requires ${_adapterType ?: "adapter"} - autofocus supported"
            _requiresAdapter -> "Requires ${_adapterType ?: "adapter"} - manual focus only"
            else -> "Compatible"
        }
    }
}
