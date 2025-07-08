// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/UserCameraBody.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class UserCameraBody private constructor() : Entity() {
    private var _id: Int = 0
    private var _userId: String = ""
    private var _cameraBodyId: Int = 0
    private var _isFavorite: Boolean = false
    private var _dateSaved: Long = 0L
    private var _customName: String? = null
    private var _serialNumber: String? = null
    private var _purchaseDate: Long? = null
    private var _notes: String = ""

    override val id: Int
        get() = _id
    val userId: String
        get() = _userId
    val cameraBodyId: Int
        get() = _cameraBodyId
    val isFavorite: Boolean
        get() = _isFavorite
    val dateSaved: Long
        get() = _dateSaved
    val customName: String?
        get() = _customName
    val serialNumber: String?
        get() = _serialNumber
    val purchaseDate: Long?
        get() = _purchaseDate
    val notes: String
        get() = _notes

    constructor(userId: String, cameraBodyId: Int) : this() {
        setUserId(userId)
        setCameraBodyId(cameraBodyId)
        _dateSaved = System.currentTimeMillis()
    }

    private fun setUserId(value: String) {
        require(value.isNotBlank()) { "User ID cannot be empty" }
        _userId = value
    }

    private fun setCameraBodyId(value: Int) {
        require(value > 0) { "Camera body ID must be positive" }
        _cameraBodyId = value
    }

    fun setAsFavorite() {
        _isFavorite = true
    }

    fun removeFromFavorites() {
        _isFavorite = false
    }

    fun setCustomName(customName: String?) {
        _customName = if (customName.isNullOrBlank()) null else customName
    }

    fun setSerialNumber(serialNumber: String?) {
        _serialNumber = if (serialNumber.isNullOrBlank()) null else serialNumber
    }

    fun setPurchaseDate(purchaseDate: Long?) {
        _purchaseDate = purchaseDate
    }

    fun addNotes(notes: String) {
        _notes = notes
    }

    fun updateNotes(notes: String) {
        _notes = notes
    }

    fun getDisplayName(): String {
        return _customName ?: "Camera ${_cameraBodyId}"
    }
}
