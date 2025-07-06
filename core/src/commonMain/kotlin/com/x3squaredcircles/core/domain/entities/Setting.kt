// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Setting.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

/**
 * User setting entity
 */
class Setting private constructor() : Entity() {
    
    private var _id: Int = 0
    private var _key: String = ""
    private var _value: String = ""
    private var _description: String = ""
    private var _timestamp: Long = 0L
    
    override val id: Int get() = _id
    val key: String get() = _key
    val value: String get() = _value
    val description: String get() = _description
    val timestamp: Long get() = _timestamp
    
    constructor(key: String, value: String, description: String = "") : this() {
        setKey(key)
        setValue(value)
        _description = description
        _timestamp = System.currentTimeMillis()
    }
    
    constructor(key: String, value: String, description: String = "", id: Int = 0) : this() {
        if (id > 0) {
            setId(id)
        }
        setKey(key)
        setValue(value)
        _description = description
        _timestamp = System.currentTimeMillis()
    }
    
    private fun setKey(value: String) {
        require(value.isNotBlank()) { "Key cannot be empty" }
        _key = value
    }
    
    private fun setValue(value: String) {
        _value = value
    }
    
    private fun setId(value: Int) {
        require(value > 0) { "Id must be greater than zero" }
        _id = value
    }
    
    fun updateValue(value: String) {
        setValue(value)
        _timestamp = System.currentTimeMillis()
    }
    
    fun getBooleanValue(): Boolean {
        return value.toBooleanStrictOrNull() == true
    }
    
    fun getIntValue(defaultValue: Int = 0): Int {
        return value.toIntOrNull() ?: defaultValue
    }
    
    fun getDateTimeValue(): Long? {
        return value.toLongOrNull()
    }
}