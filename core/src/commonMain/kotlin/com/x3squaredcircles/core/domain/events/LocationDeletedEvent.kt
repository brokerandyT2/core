// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/LocationDeletedEvent.kt
package com.x3squaredcircles.core.domain.events

import com.x3squaredcircles.core.domain.common.DomainEvent

/**
 * Domain event raised when a location is deleted
 */
class LocationDeletedEvent(
    val locationId: Int
) : DomainEvent()