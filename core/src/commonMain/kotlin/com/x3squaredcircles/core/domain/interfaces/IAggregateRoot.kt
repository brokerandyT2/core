// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/interfaces/IAggregateRoot.kt
package com.x3squaredcircles.core.domain.interfaces

/**
 * Marker interface for aggregate roots
 */
interface IAggregateRoot : IEntity {
    val domainEvents: List<IDomainEvent>
    fun addDomainEvent(eventItem: IDomainEvent)
    fun removeDomainEvent(eventItem: IDomainEvent)
    fun clearDomainEvents()
}