// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/events/OperationErrorSource.kt
package com.x3squaredcircles.photography.viewmodels.events

enum class OperationErrorSource {
    UNKNOWN,
    VALIDATION,
    DATABASE,
    NETWORK,
    SENSOR,
    PERMISSION,
    DEVICE,
    MEDIA_SERVICE,
    NAVIGATION,
    CALCULATION
}