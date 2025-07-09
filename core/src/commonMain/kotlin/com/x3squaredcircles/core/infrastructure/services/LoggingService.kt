// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/LoggingService.kt
package com.x3squaredcircles.core.infrastructure.services

import com.x3squaredcircles.core.infrastructure.external.models.LogEntry
import com.x3squaredcircles.core.enums.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoggingService : ILoggingService {

    override suspend fun logToDatabaseAsync(level: LogLevel, message: String, exception: Exception?) {
        try {
            // This would normally use a database context to insert log entries
            // For now, we'll use console logging as a fallback
            val logEntry = LogEntry(
                id = 0,
                timestamp = System.currentTimeMillis(),
                level = level.name,
                message = message,
                exception = exception?.toString() ?: ""
            )
            
            // Placeholder - would insert into database when database context is available
            println("${logEntry.level}: ${logEntry.message}")
            if (exception != null) {
                println("Exception: ${exception.message}")
            }
        } catch (ex: Exception) {
            // If we can't log to database, log to console
            println("Failed to write log to database: ${ex.message}")
            println("Original log: $level - $message")
        }
    }

    override suspend fun getLogsAsync(count: Int): List<LogEntry> {
        try {
            // This would normally query the database for log entries
            // For now, return an empty list
            return emptyList()
        } catch (ex: Exception) {
            println("Failed to retrieve logs from database: ${ex.message}")
            return emptyList()
        }
    }

    override suspend fun clearLogsAsync() {
        try {
            // This would normally execute a DELETE query on the logs table
            println("Logs cleared from database")
        } catch (ex: Exception) {
            println("Failed to clear logs from database: ${ex.message}")
            throw ex
        }
    }

    // Synchronous logging methods for non-suspend contexts
    override fun logInfo(message: String) {
        try {
            println("INFO: $message")
            // Fire and forget database logging
            CoroutineScope(Dispatchers.IO).launch {
                logToDatabaseAsync(LogLevel.INFO, message)
            }
        } catch (ex: Exception) {
            println("Failed to log info message: ${ex.message}")
        }
    }

    override fun logDebug(message: String) {
        try {
            println("DEBUG: $message")
            // Fire and forget database logging
            CoroutineScope(Dispatchers.IO).launch {
                logToDatabaseAsync(LogLevel.DEBUG, message)
            }
        } catch (ex: Exception) {
            println("Failed to log debug message: ${ex.message}")
        }
    }

    override fun logWarning(message: String, exception: Exception?) {
        try {
            println("WARN: $message")
            if (exception != null) {
                println("Exception: ${exception.message}")
            }
            // Fire and forget database logging
            CoroutineScope(Dispatchers.IO).launch {
                logToDatabaseAsync(LogLevel.WARN, message, exception)
            }
        } catch (ex: Exception) {
            println("Failed to log warning message: ${ex.message}")
        }
    }

    override fun logError(message: String, exception: Exception?) {
        try {
            println("ERROR: $message")
            if (exception != null) {
                println("Exception: ${exception.message}")
                exception.printStackTrace()
            }
            // Fire and forget database logging
            CoroutineScope(Dispatchers.IO).launch {
                logToDatabaseAsync(LogLevel.ERROR, message, exception)
            }
        } catch (ex: Exception) {
            println("Failed to log error message: ${ex.message}")
        }
    }
}