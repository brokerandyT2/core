// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/events/OperationErrorEventArgs.kt
package com.x3squaredcircles.photography.viewmodels.events

import com.x3squaredcircles.photography.viewmodels.events.OperationErrorSource

data class OperationErrorEventArgs(
    val source: OperationErrorSource,
    val message: String,
    val exception: Throwable? = null
) {
    constructor(message: String) : this(OperationErrorSource.UNKNOWN, message, null)
}