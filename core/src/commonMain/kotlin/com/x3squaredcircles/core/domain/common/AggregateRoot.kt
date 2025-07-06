// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/AggregateRoot.kt
package com.x3squaredcircles.core.domain.common

import com.x3squaredcircles.core.domain.interfaces.IAggregateRoot
import com.x3squaredcircles.core.domain.interfaces.IDomainEvent

/**
 * Represents the base class for aggregate roots in a domain-driven design (DDD) context.
 */
abstract class AggregateRoot : Entity(), IAggregateRoot {
    
    private val _domainEvents = mutableListOf<IDomainEvent>()
    
    /**
     * Gets the collection of domain events associated with the current entity.
     */
    override val domainEvents: List<IDomainEvent>
        get() = _domainEvents.toList()
    
    /**
     * Adds a domain event to the collection of events associated with the entity.
     */
    override fun addDomainEvent(eventItem: IDomainEvent) {
        _domainEvents.add(eventItem)
    }
    
    /**
     * Removes a specified domain event from the collection of domain events.
     */
    override fun removeDomainEvent(eventItem: IDomainEvent) {
        _domainEvents.remove(eventItem)
    }
    
    /**
     * Clears all domain events associated with the current entity.
     */
    override fun clearDomainEvents() {
        _domainEvents.clear()
    }
}