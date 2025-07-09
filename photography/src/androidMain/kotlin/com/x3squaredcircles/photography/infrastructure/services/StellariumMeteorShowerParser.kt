// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/StellariumMeteorShowerParser.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.core.enums.LogLevel
import com.x3squaredcircles.photography.domain.entities.MeteorShower
import com.x3squaredcircles.photography.domain.entities.MeteorShowerActivity
import com.x3squaredcircles.photography.domain.entities.MeteorShowerData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.max

class StellariumMeteorShowerParser(
    private val loggingService: ILoggingService
) {
    companion object {
        private const val MIN_ZHR_THRESHOLD = 5
    }

    suspend fun parseStellariumData(stellariumJson: String): MeteorShowerData {
        return try {
            val stellariumData = Json.decodeFromString<StellariumRoot>(stellariumJson)
            if (stellariumData.showers == null) {
                loggingService.logToDatabaseAsync(LogLevel.WARN, "Failed to parse Stellarium data or no showers found")
                return MeteorShowerData()
            }

            val showers = mutableListOf<MeteorShower>()

            stellariumData.showers.forEach { (code, showerData) ->
                try {
                    val parsedShower = parseSingleShower(code, showerData)
                    if (parsedShower != null) {
                        showers.add(parsedShower)
                    }
                } catch (ex: Exception) {
                    loggingService.logToDatabaseAsync(LogLevel.WARN, "Failed to parse shower $code", ex)
                }
            }

            loggingService.logToDatabaseAsync(LogLevel.INFO, "Successfully parsed ${showers.size} meteor showers from Stellarium data")
            MeteorShowerData(showers = showers)
        } catch (ex: Exception) {
            loggingService.logToDatabaseAsync(LogLevel.ERROR, "Failed to parse Stellarium JSON data", ex)
            MeteorShowerData()
        }
    }

    private fun parseSingleShower(code: String, showerData: StellariumShower): MeteorShower? {
        val genericActivity = showerData.activity?.firstOrNull { it.year == "generic" }
        if (genericActivity == null) {
            loggingService.logDebug( "Skipping shower $code - no generic activity data")
            return null
        }

        val activityDates = tryParseActivityDates(genericActivity)
        if (activityDates == null) {
            loggingService.logDebug("Skipping shower $code - invalid activity dates")
            return null
        }

        val zhr = parseZHR(genericActivity, code)
        if (zhr < MIN_ZHR_THRESHOLD) {
            loggingService.logDebug("Skipping shower $code - ZHR $zhr below threshold $MIN_ZHR_THRESHOLD")
            return null
        }

        val radiantCoords = tryParseRadiantCoordinates(showerData)
        if (radiantCoords == null) {
            loggingService.logDebug("Skipping shower $code - invalid radiant coordinates")
            return null
        }

        val shower = MeteorShower(
            code = code,
            designation = showerData.designation ?: code,
            activity = MeteorShowerActivity(
                start = activityDates.first,
                peak = activityDates.second,
                finish = activityDates.third,
                zhr = zhr
            ),
            radiantRA = radiantCoords.first,
            radiantDec = radiantCoords.second,
            speedKmS = showerData.speed ?: 0,
            parentBody = cleanParentBodyName(showerData.parentObj)
        )

        loggingService.logDebug("Parsed shower: $code - ${shower.designation} (ZHR: $zhr)")
        return shower
    }

    private fun tryParseActivityDates(activity: StellariumActivity): Triple<String, String, String>? {
        return try {
            val start = convertDateFormat(activity.start)
            val peak = convertDateFormat(activity.peak)
            val finish = convertDateFormat(activity.finish)

            if (start.isNotEmpty() && peak.isNotEmpty() && finish.isNotEmpty()) {
                Triple(start, peak, finish)
            } else null
        } catch (ex: Exception) {
            null
        }
    }

    private fun convertDateFormat(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""

        return try {
            val parts = dateString.trim().split("-")
            if (parts.size >= 2) {
                val month = parts[1].padStart(2, '0')
                val day = parts[0].padStart(2, '0')
                "$month-$day"
            } else ""
        } catch (ex: Exception) {
            ""
        }
    }

    private fun parseZHR(activity: StellariumActivity, code: String): Int {
        return try {
            when {
                activity.zhr != null && activity.zhr > 0 -> activity.zhr
                activity.variable?.contains("var", ignoreCase = true) == true -> estimateVariableZHR(code)
                else -> 0
            }
        } catch (ex: Exception) {
            loggingService.logWarning( "Error parsing ZHR for $code", ex)
            0
        }
    }

    private fun estimateVariableZHR(code: String): Int {
        return when (code.lowercase()) {
            "gem", "per", "qua", "lyr" -> 60
            "dra", "leo", "ori" -> 20
            else -> 10
        }
    }

    private fun tryParseRadiantCoordinates(showerData: StellariumShower): Pair<Double, Double>? {
        return try {
            val raStr = showerData.radiantAlpha?.trim()
            val decStr = showerData.radiantDelta?.trim()

            if (raStr.isNullOrEmpty() || decStr.isNullOrEmpty()) return null

            val ra = parseCoordinate(raStr)
            val dec = parseCoordinate(decStr)

            if (ra != null && dec != null) {
                Pair(ra, dec)
            } else null
        } catch (ex: Exception) {
            null
        }
    }

    private fun parseCoordinate(coordStr: String): Double? {
        return try {
            if (coordStr.contains(":")) {
                val parts = coordStr.split(":")
                val hours = parts[0].toDouble()
                val minutes = if (parts.size > 1) parts[1].toDouble() else 0.0
                val seconds = if (parts.size > 2) parts[2].toDouble() else 0.0
                hours + minutes / 60.0 + seconds / 3600.0
            } else {
                coordStr.toDouble()
            }
        } catch (ex: Exception) {
            null
        }
    }

    private fun cleanParentBodyName(parentObj: String?): String {
        if (parentObj.isNullOrEmpty()) return ""

        return parentObj
            .replace("Comet ", "")
            .replace("Minor planet ", "")
            .trim()
    }
}

@Serializable
data class StellariumRoot(
    @SerialName("shortName")
    val shortName: String? = null,
    @SerialName("version")
    val version: Int = 0,
    @SerialName("showers")
    val showers: Map<String, StellariumShower>? = null
)

@Serializable
data class StellariumShower(
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("activity")
    val activity: List<StellariumActivity>? = null,
    @SerialName("radiantAlpha")
    val radiantAlpha: String? = null,
    @SerialName("radiantDelta")
    val radiantDelta: String? = null,
    @SerialName("speed")
    val speed: Int? = null,
    @SerialName("parentObj")
    val parentObj: String? = null,
    @SerialName("driftAlpha")
    val driftAlpha: String? = null,
    @SerialName("driftDelta")
    val driftDelta: String? = null,
    @SerialName("pidx")
    val pidx: Double? = null,
    @SerialName("colors")
    val colors: List<StellariumColor>? = null
)

@Serializable
data class StellariumActivity(
    @SerialName("year")
    val year: String? = null,
    @SerialName("zhr")
    val zhr: Int? = null,
    @SerialName("variable")
    val variable: String? = null,
    @SerialName("start")
    val start: String? = null,
    @SerialName("finish")
    val finish: String? = null,
    @SerialName("peak")
    val peak: String? = null
)

@Serializable
data class StellariumColor(
    @SerialName("color")
    val color: String? = null,
    @SerialName("intensity")
    val intensity: Int = 0
)