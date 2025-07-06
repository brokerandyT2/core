// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Tip.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.Entity

/**
 * Photography tip entity
 */
class Tip private constructor() : Entity() {
    
    private var _title: String = ""
    private var _content: String = ""
    private var _fstop: String = ""
    private var _shutterSpeed: String = ""
    private var _iso: String = ""
    private var _i8n: String = "en-US"
    private var _tipTypeId: Int = 0
    
    val tipTypeId: Int get() = _tipTypeId
    val title: String get() = _title
    val content: String get() = _content
    val fstop: String get() = _fstop
    val shutterSpeed: String get() = _shutterSpeed
    val iso: String get() = _iso
    val i8n: String get() = _i8n
    
    constructor(tipTypeId: Int, title: String, content: String) : this() {
        _tipTypeId = tipTypeId
        setTitle(title)
        setContent(content)
    }
    
    private fun setTitle(value: String) {
        require(value.isNotBlank()) { "Title cannot be empty" }
        _title = value
    }
    
    private fun setContent(value: String) {
        _content = value
    }
    
    fun updatePhotographySettings(fstop: String?, shutterSpeed: String?, iso: String?) {
        _fstop = fstop ?: ""
        _shutterSpeed = shutterSpeed ?: ""
        _iso = iso ?: ""
    }
    
    fun updateContent(title: String, content: String) {
        setTitle(title)
        setContent(content)
    }
    
    fun setLocalization(i8n: String?) {
        _i8n = i8n ?: "en-US"
    }
}