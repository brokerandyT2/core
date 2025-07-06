// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/interfaces/IDomainEvent.kt
package com.x3squaredcircles.core.domain.interfaces

import java.time.Instant

/**
 * Marker interface for domain events
 */
interface IDomainEvent {
    val dateOccurred: Long
}