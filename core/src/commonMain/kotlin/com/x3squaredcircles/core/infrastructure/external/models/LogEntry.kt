// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/ILoggingService.kt
package com.x3squaredcircles.core.infrastructure.external.models

data class LogEntry(
    val id: Int,
    val timestamp: Long,
    val level: String,
    val message: String,
    val exception: String
)
