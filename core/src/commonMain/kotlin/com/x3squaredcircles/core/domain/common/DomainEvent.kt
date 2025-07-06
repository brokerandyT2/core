// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/DomainEvent.kt
package com.x3squaredcircles.core.domain.common

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent

/**
 * Base class for all domain events
 */
abstract class DomainEvent : IDomainEvent {
    
    override val dateOccurred: Long = System.currentTimeMillis()
}