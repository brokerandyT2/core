// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/ITimezoneService.kt
package com.x3squaredcircles.core.infrastructure.services

interface ITimezoneService {
    fun convertUtcToLocal(utcTimestamp: Long): Long
    fun convertLocalToUtc(localTimestamp: Long): Long
    fun getCurrentTimezone(): String
    fun getTimezoneOffset(): Int
    fun formatDateTime(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String
    fun formatDate(timestamp: Long, pattern: String = "yyyy-MM-dd"): String
    fun formatTime(timestamp: Long, pattern: String = "HH:mm:ss"): String
}