// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/domain/models/EnhancedSunTimes.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.LocalDateTime

data class EnhancedSunTimes(
    val sunrise: LocalDateTime = LocalDateTime(2024, 1, 1, 6, 0),
    val sunset: LocalDateTime = LocalDateTime(2024, 1, 1, 18, 0),
    val solarNoon: LocalDateTime = LocalDateTime(2024, 1, 1, 12, 0),
    val civilDawn: LocalDateTime = LocalDateTime(2024, 1, 1, 5, 30),
    val civilDusk: LocalDateTime = LocalDateTime(2024, 1, 1, 18, 30),
    val nauticalDawn: LocalDateTime = LocalDateTime(2024, 1, 1, 5, 0),
    val nauticalDusk: LocalDateTime = LocalDateTime(2024, 1, 1, 19, 0),
    val astronomicalDawn: LocalDateTime = LocalDateTime(2024, 1, 1, 4, 30),
    val astronomicalDusk: LocalDateTime = LocalDateTime(2024, 1, 1, 19, 30),
    val blueHourMorning: LocalDateTime = LocalDateTime(2024, 1, 1, 5, 45),
    val blueHourEvening: LocalDateTime = LocalDateTime(2024, 1, 1, 18, 15),
    val goldenHourMorningStart: LocalDateTime = LocalDateTime(2024, 1, 1, 6, 0),
    val goldenHourMorningEnd: LocalDateTime = LocalDateTime(2024, 1, 1, 7, 0),
    val goldenHourEveningStart: LocalDateTime = LocalDateTime(2024, 1, 1, 17, 0),
    val goldenHourEveningEnd: LocalDateTime = LocalDateTime(2024, 1, 1, 18, 0),
    val timeZone: String = "UTC",
    val isDaylightSavingTime: Boolean = false,
    val utcOffsetMinutes: Int = 0,
    val solarTimeOffsetMinutes: Int = 0
)