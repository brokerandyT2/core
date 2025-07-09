// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/MeteorShower.kt
package com.x3squaredcircles.photography.domain.entities

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.math.*

/**
 * Represents a meteor shower with its activity periods and characteristics
 */
data class MeteorShower(
    /**
     * Short code identifier (e.g., "PER", "GEM", "LYR")
     */
    val code: String = "",

    /**
     * Full display name (e.g., "Perseids", "Geminids", "Lyrids")
     */
    val designation: String = "",

    /**
     * Activity period with start, peak, and finish dates
     */
    val activity: MeteorShowerActivity = MeteorShowerActivity(),

    /**
     * Right Ascension of radiant in degrees
     */
    val radiantRA: Double = 0.0,

    /**
     * Declination of radiant in degrees
     */
    val radiantDec: Double = 0.0,

    /**
     * Meteoroid velocity in km/s
     */
    val speedKmS: Int = 0,

    /**
     * Parent comet or asteroid that creates this meteor shower
     */
    val parentBody: String = ""
) {
    /**
     * Checks if this shower is active on the given date
     */
    fun isActiveOn(date: LocalDate): Boolean {
        return activity.isActiveOn(date)
    }

    /**
     * Gets the expected ZHR for the given date during the shower
     */
    fun getExpectedZHR(date: LocalDate): Double {
        if (!isActiveOn(date)) return 0.0
        return activity.getExpectedZHR(date)
    }

    /**
     * Calculates radiant position for given observer coordinates and date
     */
    fun calculateRadiantPosition(
        observerLatitude: Double,
        observerLongitude: Double,
        date: LocalDate
    ): RadiantPosition {
        // Simplified calculation - in production would use proper astronomical calculations
        val altitude = calculateAltitude(radiantDec, observerLatitude, 0.0)
        val azimuth = calculateAzimuth(radiantDec, observerLatitude, 0.0, altitude)
        
        return RadiantPosition(
            altitude = altitude,
            azimuth = azimuth,
            isVisible = altitude > 0
        )
    }

    private fun calculateAltitude(declination: Double, latitude: Double, hourAngle: Double): Double {
        val decRad = Math.toRadians(declination)
        val latRad = Math.toRadians(latitude)
        val haRad = Math.toRadians(hourAngle)

        val sinAlt = sin(decRad) * sin(latRad) + cos(decRad) * cos(latRad) * cos(haRad)
        return Math.toDegrees(asin(sinAlt.coerceIn(-1.0, 1.0)))
    }

    private fun calculateAzimuth(declination: Double, latitude: Double, hourAngle: Double, altitude: Double): Double {
        val decRad = Math.toRadians(declination)
        val latRad = Math.toRadians(latitude)
        val haRad = Math.toRadians(hourAngle)
        val altRad = Math.toRadians(altitude)

        val cosAz = (sin(decRad) - sin(altRad) * sin(latRad)) / (cos(altRad) * cos(latRad))
        var azimuth = Math.toDegrees(acos(cosAz.coerceIn(-1.0, 1.0)))

        if (sin(haRad) > 0) {
            azimuth = 360.0 - azimuth
        }

        return azimuth
    }
}

/**
 * Represents the activity period and intensity of a meteor shower
 */
data class MeteorShowerActivity(
    /**
     * Start date in MM-DD format (e.g., "07-17")
     */
    val start: String = "",

    /**
     * Peak date in MM-DD format (e.g., "08-12")
     */
    val peak: String = "",

    /**
     * Finish date in MM-DD format (e.g., "08-24")
     */
    val finish: String = "",

    /**
     * Zenith Hourly Rate at peak
     */
    val zhr: Int = 0
) {
    /**
     * Checks if this shower is active on the given date
     */
    fun isActiveOn(date: LocalDate): Boolean {
        return try {
            val startDate = parseActivityDate(start, date.year)
            val finishDate = parseActivityDate(finish, date.year)

            // Handle year boundary crossing (e.g., Quadrantids: Dec-Jan)
            if (startDate > finishDate) {
                // Shower crosses year boundary
                val startDatePrevYear = parseActivityDate(start, date.year - 1)
                val finishDateNextYear = parseActivityDate(finish, date.year + 1)
                
                date >= startDate || date <= finishDate ||
                (date.year > startDate.year && date <= finishDateNextYear) ||
                (date.year < finishDate.year && date >= startDatePrevYear)
            } else {
                date >= startDate && date <= finishDate
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Gets the expected ZHR for the given date during the shower
     */
    fun getExpectedZHR(date: LocalDate): Double {
        if (!isActiveOn(date)) return 0.0

        return try {
            val peakDate = parseActivityDate(peak, date.year)
            val daysDifference = kotlin.math.abs(date.toEpochDays() - peakDate.toEpochDays())

            when {
                daysDifference.toLong() == 0L -> zhr.toDouble() // Peak day
                daysDifference.toLong() == 1L -> zhr * 0.8 // 80% of peak
                daysDifference <= 2L -> zhr * 0.6 // 60% of peak
                daysDifference <= 3L -> zhr * 0.4 // 40% of peak
                else -> zhr * 0.2 // 20% of peak
            }
        } catch (e: Exception) {
            zhr * 0.5 // Default to half peak if date parsing fails
        }
    }

    private fun parseActivityDate(activityDate: String, year: Int): LocalDate {
        require(activityDate.isNotEmpty()) { "Activity date cannot be empty" }

        val parts = activityDate.split('-')
        require(parts.size == 2) { "Invalid activity date format: $activityDate" }

        val month = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid month in date: $activityDate")
        val day = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid day in date: $activityDate")

        return LocalDate(year, month, day)
    }
}

/**
 * Represents the position of a meteor shower radiant in the sky
 */
data class RadiantPosition(
    /**
     * Altitude above horizon in degrees
     */
    val altitude: Double,

    /**
     * Azimuth from north in degrees
     */
    val azimuth: Double,

    /**
     * Whether the radiant is above the horizon
     */
    val isVisible: Boolean
) {
    /**
     * Gets a descriptive string for the altitude
     */
    val altitudeDescription: String
        get() = when {
            altitude >= 60 -> "High in sky"
            altitude >= 30 -> "Moderate altitude"
            altitude >= 10 -> "Low on horizon"
            altitude > 0 -> "Just above horizon"
            else -> "Below horizon"
        }

    /**
     * Gets a descriptive string for the azimuth direction
     */
    val directionDescription: String
        get() = when (azimuth) {
            in 337.5..360.0, in 0.0..22.5 -> "North"
            in 22.5..67.5 -> "Northeast"
            in 67.5..112.5 -> "East"
            in 112.5..157.5 -> "Southeast"
            in 157.5..202.5 -> "South"
            in 202.5..247.5 -> "Southwest"
            in 247.5..292.5 -> "West"
            in 292.5..337.5 -> "Northwest"
            else -> "Unknown"
        }
}

/**
 * Container for multiple meteor showers data
 */
data class MeteorShowerData(
    /**
     * List of all meteor showers
     */
    val showers: List<MeteorShower> = emptyList()
) {
    /**
     * Gets all showers active on the specified date
     */
    fun getActiveShowers(date: LocalDate): List<MeteorShower> {
        return showers.filter { it.isActiveOn(date) }
    }

    /**
     * Gets showers active on the specified date with minimum ZHR
     */
    fun getActiveShowers(date: LocalDate, minZHR: Int): List<MeteorShower> {
        return showers
            .filter { it.isActiveOn(date) && it.getExpectedZHR(date) >= minZHR }
            .sortedByDescending { it.getExpectedZHR(date) }
    }

    /**
     * Gets a shower by its code
     */
    fun getShowerByCode(code: String): MeteorShower? {
        return showers.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}