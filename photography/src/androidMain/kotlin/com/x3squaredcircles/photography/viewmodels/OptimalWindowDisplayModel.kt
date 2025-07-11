// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/viewmodels/OptimalWindowDisplayModel.kt
package com.x3squaredcircles.photography.viewmodels

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class OptimalWindowDisplayModel(
    val windowType: String = "",
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val startTimeDisplay: String = "",
    val endTimeDisplay: String = "",
    val lightQuality: String = "",
    val optimalFor: String = "",
    val isCurrentlyActive: Boolean = false,
    val confidenceLevel: Double = 0.0,
    val timeFormat: String = "HH:mm"
) {
    val isOptimalTime: Boolean
        get() = isCurrentlyActive || confidenceLevel >= 0.7
    
    val formattedTimeRange: String
        get() {
            val formatter = DateTimeFormatter.ofPattern(timeFormat)
            return "${startTime.toJavaLocalDateTime().format(formatter)} - ${endTime.toJavaLocalDateTime().format(formatter)}"
        }
    
    val duration: Duration
        get() = (endTime.toInstant(TimeZone.UTC).toEpochMilliseconds() - startTime.toInstant(TimeZone.UTC).toEpochMilliseconds()).milliseconds
    
    val durationDisplay: String
        get() {
            val hours = duration.inWholeHours
            val minutes = (duration.inWholeMinutes % 60)
            return "${hours}h ${minutes}m"
        }
    
    val confidenceDisplay: String
        get() = "${(confidenceLevel * 100).toInt()}% confidence"
    
    fun getFormattedStartTime(timeFormat: String): String {
        val formatter = DateTimeFormatter.ofPattern(timeFormat)
        return startTime.toJavaLocalDateTime().format(formatter)
    }
    
    fun getFormattedEndTime(timeFormat: String): String {
        val formatter = DateTimeFormatter.ofPattern(timeFormat)
        return endTime.toJavaLocalDateTime().format(formatter)
    }
    
    fun getFormattedTimeRange(timeFormat: String): String {
        val formatter = DateTimeFormatter.ofPattern(timeFormat)
        return "${startTime.toJavaLocalDateTime().format(formatter)} - ${endTime.toJavaLocalDateTime().format(formatter)}"
    }
}