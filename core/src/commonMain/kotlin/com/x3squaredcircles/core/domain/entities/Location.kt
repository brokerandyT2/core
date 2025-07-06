// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Location.kt
package com.x3squaredcircles.core.domain.entities

import com.x3squaredcircles.core.domain.common.AggregateRoot
import com.x3squaredcircles.core.domain.events.LocationSavedEvent
import com.x3squaredcircles.core.domain.events.PhotoAttachedEvent
import com.x3squaredcircles.core.domain.events.LocationDeletedEvent
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.Address

/**
 * Location aggregate root
 */
class Location private constructor() : AggregateRoot() {
    
    private var _id: Int = 0
    private var _title: String = ""
    private var _description: String = ""
    private var _coordinate: Coordinate? = null
    private var _address: Address? = null
    private var _photoPath: String? = null
    private var _isDeleted: Boolean = false
    private var _timestamp: Long = 0L
    
    override val id: Int get() = _id
    
    val title: String get() = _title
    val description: String get() = _description
    val coordinate: Coordinate? get() = _coordinate
    val address: Address? get() = _address
    val photoPath: String? get() = _photoPath
    val isDeleted: Boolean get() = _isDeleted
    val timestamp: Long get() = _timestamp
    
    constructor(title: String, description: String, coordinate: Coordinate, address: Address) : this() {
        setTitle(title)
        setDescription(description)
        _coordinate = coordinate
        _address = address
        _timestamp = System.currentTimeMillis()
        
        addDomainEvent(LocationSavedEvent(this))
    }
    
    private fun setTitle(value: String) {
        require(value.isNotBlank()) { "Title cannot be empty" }
        _title = value
    }
    
    private fun setDescription(value: String) {
        _description = value
    }
    
    private fun setId(value: Int) {
        require(value > 0) { "Id must be greater than zero" }
        _id = value
    }
    
    fun updateDetails(title: String, description: String) {
        setTitle(title)
        setDescription(description)
        addDomainEvent(LocationSavedEvent(this))
    }
    
    fun updateCoordinate(coordinate: Coordinate) {
        _coordinate = coordinate
        addDomainEvent(LocationSavedEvent(this))
    }
    
    fun attachPhoto(photoPath: String) {
        require(photoPath.isNotBlank()) { "Photo path cannot be empty" }
        
        _photoPath = photoPath
        addDomainEvent(PhotoAttachedEvent(id, photoPath))
    }
    
    fun removePhoto() {
        _photoPath = null
    }
    
    fun delete() {
        _isDeleted = true
        addDomainEvent(LocationDeletedEvent(id))
    }
    
    fun restore() {
        _isDeleted = false
    }
}