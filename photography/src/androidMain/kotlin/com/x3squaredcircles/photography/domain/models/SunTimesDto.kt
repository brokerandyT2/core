// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SunTimesDto.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class SunTimesDto(
    val date: LocalDate,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    val solarNoon: LocalDateTime,
    val astronomicalDawn: LocalDateTime,
    val astronomicalDusk: LocalDateTime,
    val nauticalDawn: LocalDateTime,
    val nauticalDusk: LocalDateTime,
    val civilDawn: LocalDateTime,
    val civilDusk: LocalDateTime,
    val goldenHourMorningStart: LocalDateTime,
    val goldenHourMorningEnd: LocalDateTime,
    val goldenHourEveningStart: LocalDateTime,
    val goldenHourEveningEnd: LocalDateTime
)