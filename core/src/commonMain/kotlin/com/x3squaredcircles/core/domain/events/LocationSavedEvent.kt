// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/LocationSavedEvent.kt
package com.x3squaredcircles.core.domain.events

import com.x3squaredcircles.core.domain.common.DomainEvent
import com.x3squaredcircles.core.domain.entities.Location

/**
 * Domain event raised when a location is saved
 */
class LocationSavedEvent(
    val location: Location
) : DomainEvent()