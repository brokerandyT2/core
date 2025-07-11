// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SunTimesDto.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class SunTimesDto(
    var date: LocalDate,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var sunrise: LocalDateTime,
    var sunset: LocalDateTime,
    var solarNoon: LocalDateTime,
    var astronomicalDawn: LocalDateTime,
    var astronomicalDusk: LocalDateTime,
    var nauticalDawn: LocalDateTime,
    var nauticalDusk: LocalDateTime,
    var civilDawn: LocalDateTime,
    var civilDusk: LocalDateTime,
    var goldenHourMorningStart: LocalDateTime,
    var goldenHourMorningEnd: LocalDateTime,
    var goldenHourEveningStart: LocalDateTime,
    var goldenHourEveningEnd: LocalDateTime
)