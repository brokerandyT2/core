// core/src/commonMain/kotlin/com/x3squaredcircles/core/events/DomainErrorEvent.kt
package com.x3squaredcircles.core.events

import java.util.*
import com.x3squaredcircles.core.events.ErrorSeverity

/**
 *
 * Base class for all domain-specific error events
 */
abstract class DomainErrorEvent(val source: String) {
    val errorId: String = UUID.randomUUID().toString()
    val timestamp: Long = System.currentTimeMillis()
    /**
     *
     * Gets the resource key for localized error message
     */
    abstract val resourceKey: String

    /**
     *
     * Gets parameters for message formatting
     */
    open val parameters: Map<String, Any>
        get() = emptyMap()

    /**
     *
     * Gets the error severity level
     */
    open val severity: ErrorSeverity
        get() = ErrorSeverity.Error
}
