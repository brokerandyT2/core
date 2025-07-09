package com.x3squaredcircles.core.infrastructure.services

import com.x3squaredcircles.core.enums.LogLevel
import com.x3squaredcircles.core.infrastructure.external.models.LogEntry

interface ILoggingService {
    suspend fun logToDatabaseAsync(level: LogLevel, message: String, exception: Exception? = null)
    suspend fun getLogsAsync(count: Int = 100): List<LogEntry>
    suspend fun clearLogsAsync()

    fun logInfo(message: String)
    fun logDebug(message: String)
    fun logWarning(message: String, exception: Exception? = null)
    fun logError(message: String, exception: Exception? = null)
}
