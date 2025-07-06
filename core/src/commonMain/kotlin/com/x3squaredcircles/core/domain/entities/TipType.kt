// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/TipType.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.core.domain.entities.Tip

/**
 * Tip category entity
 */
class TipType private constructor() : Entity() {
    
    private var _name: String = ""
    private var _i8n: String = "en-US"
    private val _tips = mutableListOf<Tip>()
    
    val name: String get() = _name
    val i8n: String get() = _i8n
    val tips: List<Tip> get() = _tips.toList()
    
    constructor(name: String) : this() {
        setName(name)
    }
    
    private fun setName(value: String) {
        require(value.isNotBlank()) { "Name cannot be empty" }
        _name = value
    }
    
    fun setLocalization(i8n: String?) {
        _i8n = i8n ?: "en-US"
    }
    
    fun addTip(tip: Tip) {
        require(tip.tipTypeId == id || id == 0) { "Tip type ID mismatch" }
        _tips.add(tip)
    }
    
    fun removeTip(tip: Tip) {
        _tips.remove(tip)
    }
}
