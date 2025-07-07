// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Subscription.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

class Subscription private constructor() : Entity() {
    private var _id: Int = 0
    private var _userId: String = ""
    private var _status: String = ""
    private var _expirationDate: Long = 0L
    private var _createdDate: Long = 0L
    private var _updatedDate: Long = 0L

    override val id: Int
        get() = _id
    val userId: String
        get() = _userId
    val status: String
        get() = _status
    val expirationDate: Long
        get() = _expirationDate
    val createdDate: Long
        get() = _createdDate
    val updatedDate: Long
        get() = _updatedDate

    constructor(userId: String, status: String, expirationDate: Long) : this() {
        setUserId(userId)
        setStatus(status)
        _expirationDate = expirationDate
        _createdDate = System.currentTimeMillis()
        _updatedDate = System.currentTimeMillis()
    }

    private fun setUserId(value: String) {
        require(value.isNotBlank()) { "User ID cannot be empty" }
        _userId = value
    }

    private fun setStatus(value: String) {
        require(value.isNotBlank()) { "Status cannot be empty" }
        _status = value
    }

    fun updateStatus(status: String) {
        setStatus(status)
        _updatedDate = System.currentTimeMillis()
    }

    fun updateExpirationDate(expirationDate: Long) {
        _expirationDate = expirationDate
        _updatedDate = System.currentTimeMillis()
    }

    fun isActive(): Boolean {
        return status == "active" && expirationDate > System.currentTimeMillis()
    }

    fun isExpired(): Boolean {
        return expirationDate <= System.currentTimeMillis()
    }
}
